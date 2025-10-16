package com.web.back.process;

import com.web.back.model.dto.FieldValue;
import com.web.back.model.dto.evaluation.TheoreticalScheduleDto;
import com.web.back.model.dto.evaluation.TheoreticalTimesheetDay;
import com.web.back.model.entities.*;
import com.web.back.model.enumerators.ScheduleStatus;
import com.web.back.repositories.*;
import com.web.back.utils.FieldValueDictionary;
import org.apache.commons.jexl3.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
@Transactional
public class EvaluationAtLevelOneProcess {
    private final TimeRecordRepository timeRecordRepository;
    private final TimeRuleRepository timeRuleRepository;
    private final TimesheetTimeRuleRepository timesheetTimeRuleRepository;
    private final EmployeeTimesheetsRepository employeeTimesheetsRepository;
    private final FestiveDaysRepository festiveDaysRepository;
    private final EvaluationRepository evaluationRepository;

    public EvaluationAtLevelOneProcess(TimeRecordRepository timeRecordRepository,
                             TimeRuleRepository timeRuleRepository,
                             TimesheetTimeRuleRepository timesheetTimeRuleRepository,
                             EmployeeTimesheetsRepository employeeTimesheetsRepository,
                             FestiveDaysRepository festiveDaysRepository,
                             EvaluationRepository evaluationRepository) {
        this.timeRecordRepository = timeRecordRepository;
        this.timeRuleRepository = timeRuleRepository;
        this.timesheetTimeRuleRepository = timesheetTimeRuleRepository;
        this.employeeTimesheetsRepository = employeeTimesheetsRepository;
        this.festiveDaysRepository = festiveDaysRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public void generateEvaluations(Instant beginDate, Instant endDate) {
        List<TimeRecord> timeRecords = timeRecordRepository.findAllByDateBetween(beginDate, endDate);

        var employees = timeRecords.stream()
                .map(TimeRecord::getEmployee)
                .distinct()
                .toList();

        var employeeIds = employees.stream()
                .map(Employee::getId)
                .distinct()
                .toList();

        List<EmployeeTimesheet> timeSheetEmployees = employeeTimesheetsRepository.findOverlappingTimesheetsByIds(employeeIds, beginDate, endDate);

        var timeSheetIds = timeSheetEmployees.stream()
                .map(tse -> tse.getTimesheet().getId())
                .distinct()
                .toList();

        var timeSheets = timeSheetEmployees.stream()
                .map(EmployeeTimesheet::getTimesheet)
                .filter(timesheet -> !timeSheetIds.contains(timesheet.getId()))
                .distinct()
                .toList();

        List<TimesheetTimeRule> timesheetTimeRules = timesheetTimeRuleRepository.findAllByTimesheetIdIn(timeSheetIds);

        var timeRuleIds = timesheetTimeRules.stream()
                .map(tr -> tr.getTimeRule().getId())
                .distinct()
                .toList();

        List<TimeRule> timeRuleEntities = timeRuleRepository.findAllById(timeRuleIds);

        List<FestiveDay> festiveDays = festiveDaysRepository.findAll();

        var theoreticalSchedulesWithTimeRecords = generateTheoreticalSchedule(
                employeeIds, timeRecords, timeSheetEmployees, festiveDays, beginDate, endDate);

        executeLevelOneRules(theoreticalSchedulesWithTimeRecords, timesheetTimeRules, timeRuleEntities);
        saveEvaluations(theoreticalSchedulesWithTimeRecords, employees, timeSheets);
        // Future implementation for level 2
    }

    private void saveEvaluations(List<TheoreticalScheduleDto> theoreticalSchedulesWithTimeRecords,
                                 List<Employee> employees,
                                 List<Timesheet> timesheets) {
        for (var schedule : theoreticalSchedulesWithTimeRecords) {
            for (var dayAndRecord : schedule.scheduleAndTimeRecords()) {
                var employeeId = UUID.fromString(schedule.employeeId());
                var day = dayAndRecord.getT1();
                var generalResult = day.generalResult();
                LocalTime entryTime = null;
                LocalTime breakDepartureTime = null;
                LocalTime breakReturnTime = null;
                LocalTime departureTime = null;
                Integer turn = null;
                var employee = employees.stream()
                        .filter(e -> e.getId().equals(employeeId))
                        .findFirst();
                var timesheet = timesheets.stream()
                        .filter(t -> t.getId().equals(day.timeSheetId()))
                        .findFirst();
                var timesheetIdentifier = timesheet.map(Timesheet::getTimesheetIdentifier).orElse(null);

                var recordOpt = dayAndRecord.getT2();
                if (recordOpt.isPresent()) {
                    var record = recordOpt.get();
                    entryTime = record.getEntryTime();
                    breakDepartureTime = record.getBreakDepartureTime();
                    breakReturnTime = record.getBreakReturnTime();
                    departureTime = record.getDepartureTime();
                    turn = record.getTurn();
                }

                Evaluation evaluation = new Evaluation();
                evaluation.setResultadoGeneral(generalResult);
                evaluation.setFecha(day.date().atZone(ZoneId.systemDefault()).toLocalDate());
                evaluation.setStatusRegistro(null);
                evaluation.setTurn(turn);

                // Do I need to store the time record in the evaluation entity?
                if(entryTime != null){
                    evaluation.setHoraEntrada(Time.valueOf(entryTime));
                }
                if (breakDepartureTime != null){
                    evaluation.setHoraPausa(Time.valueOf(breakDepartureTime));
                }
                if (breakReturnTime != null){
                    evaluation.setHoraRegresoPausa(Time.valueOf(breakReturnTime));
                }
                if (departureTime != null) {
                    evaluation.setHoraSalida(Time.valueOf(departureTime));
                }

                // In the future set the employee and timesheet ids instead
                evaluation.setHorario(timesheetIdentifier);
                evaluation.setEmployeeName(employee.get().getName());
                evaluation.setNumEmpleado(employee.get().getNumEmployee());

                evaluationRepository.save(evaluation);
            }
        }
    }

    private void executeLevelOneRules(List<TheoreticalScheduleDto> theoreticalSchedulesWithTimeRecords,
                                      List<TimesheetTimeRule> timesheetTimeRules,
                                      List<TimeRule> timeRuleEntities) {
        var levelOneRules = timeRuleEntities.stream()
                .filter(tr -> tr.getLevel() == 1)
                .sorted(Comparator.comparing(TimeRule::getSequence))
                .toList();

        for (var schedule : theoreticalSchedulesWithTimeRecords) {
            for (var dayAndRecord : schedule.scheduleAndTimeRecords()) {
                var day = dayAndRecord.getT1();
                var recordOpt = dayAndRecord.getT2();

                List<String> generalResults = new ArrayList<>();
                for (var rule : levelOneRules) {
                    var ruleAppliesFroCurrentTimesheet = timesheetTimeRules.stream()
                            .anyMatch(ttr ->
                                    ttr.getTimesheet().getId().equals(day.timeSheetId()) &&
                                            ttr.getTimeRule().getId().equals(rule.getId()));

                    if (!ruleAppliesFroCurrentTimesheet) continue;

                    var fieldValues = getFieldValues(day, recordOpt.orElse(null));
                    boolean conditionsMet = evaluateRule(rule.getRule(), fieldValues);
                    if (conditionsMet &&
                            rule.getResultMeets() != null &&
                            !rule.getResultMeets().isEmpty()) {
                        generalResults.add(rule.getResultMeets());
                    }

                    if(rule.isExclusive()) break;
                }

                if(!generalResults.isEmpty()) {
                    day.withGeneralResult(String.join("|", generalResults));
                }
            }
        }
    }

    private static Map<String, Object> getFieldValues(TheoreticalTimesheetDay day, TimeRecord record) {
        Map<String, Object> values = new HashMap<>();
        for (FieldValue fv : FieldValueDictionary.getDictionary()) {
            Object source = fv.getSourceClass().equals(TimeRecord.class) ? record : day;
            try {
                Field field = fv.getSourceClass().getDeclaredField(fv.getFieldName());
                field.setAccessible(true);
                Object value = field.get(source);

                // Transform java.sql.Time to minutes
                if (value instanceof Time) {
                    value = ((Time) value).toLocalTime().getHour() * 60 + ((Time) value).toLocalTime().getMinute();
                }

                if (value instanceof LocalTime) {
                    value = ((LocalTime) value).getHour() * 60 + ((LocalTime) value).getMinute();
                }

                if (value instanceof Instant) {
                    value = ((Instant) value).toEpochMilli();
                }

                values.put(fv.getKey(), value);
            } catch (Exception e) {
                values.put(fv.getKey(), null);
            }
        }
        return values;
    }

    public static boolean evaluateRule(String rule, Map<String, Object> fieldValues) {
        JexlEngine jexl = new JexlBuilder().create();
        JexlExpression expression = jexl.createExpression(rule);
        JexlContext context = new MapContext(fieldValues);

        Object result = expression.evaluate(context);
        if (result instanceof Boolean) {
            return (Boolean) result;
        } else {
            throw new IllegalArgumentException("Rule did not evaluate to a boolean value");
        }
    }

    private List<TheoreticalScheduleDto> generateTheoreticalSchedule(List<UUID> employeeIds,
                                                                     List<TimeRecord> timeRecords,
                                                                     List<EmployeeTimesheet> timeSheetEmployees,
                                                                     List<FestiveDay> festiveDays,
                                                                     Instant beginDate,
                                                                     Instant endDate) {
        List<TheoreticalScheduleDto> result = new ArrayList<>();

        for (Instant date = beginDate; !date.isAfter(endDate); date = date.plus(Duration.ofDays(1))) {
            Instant currentDate = date;
            boolean isFestive = festiveDays.stream()
                    .anyMatch(fd -> fd.getDay().equals(currentDate.get(ChronoField.DAY_OF_MONTH))
                            && fd.getMonth().equals(currentDate.get(ChronoField.MONTH_OF_YEAR)));

            if (isFestive) {
                for (UUID employeeId : employeeIds) {
                    addScheduleDay(result, employeeId, date, null, ScheduleStatus.FERI, timeRecords, currentDate);
                }
                continue;
            }

            var timesheetsForDate = timeSheetEmployees.stream()
                    .filter(ets ->
                            !currentDate.isBefore(ets.getFromDate()) &&
                            !currentDate.isAfter(ets.getToDate()) &&
                            ets.getTimesheet().getDaysOfTheWeek() == currentDate.get(ChronoField.DAY_OF_WEEK) &&
                            ((ets.getTimesheet().getDaysOfTheWeek() & (1 << (currentDate.get(ChronoField.DAY_OF_WEEK)))) != 0)
                    )
                    .toList();

            for (var employeeTimesheet : timesheetsForDate) {
                var employeeId = employeeTimesheet.getEmployee().getId();
                var timesheet = employeeTimesheet.getTimesheet();
                addScheduleDay(result, employeeId, date, timesheet, ScheduleStatus.NORM, timeRecords, currentDate);
            }

            var employeesWithoutTimesheets = employeeIds.stream()
                    .filter(eid -> timesheetsForDate.stream()
                            .noneMatch(ets -> ets.getEmployee().getId().equals(eid)))
                    .toList();

            for (UUID employeeId : employeesWithoutTimesheets) {
                addScheduleDay(result, employeeId, date, null, ScheduleStatus.DESC, timeRecords, currentDate);
            }
        }

        return result;
    }

    private void addScheduleDay(List<TheoreticalScheduleDto> result,
                                UUID employeeId,
                                Instant date,
                                Timesheet timesheet,
                                ScheduleStatus status,
                                List<TimeRecord> timeRecords,
                                Instant currentDate) {
        TheoreticalTimesheetDay theoreticalDay = getTheoreticalDay(date, timesheet, status);

        var employeeTimeRecord = timeRecords.stream()
                .filter(tr -> tr.getEmployee().getId().equals(employeeId) && tr.getDate().equals(currentDate))
                .findFirst();

        TheoreticalScheduleDto dto = result.stream()
                .filter(d -> d.employeeId().equals(employeeId.toString()))
                .findFirst()
                .orElseGet(() -> {
                    TheoreticalScheduleDto newDto = new TheoreticalScheduleDto(employeeId.toString(), new ArrayList<>());
                    result.add(newDto);
                    return newDto;
                });

        dto.scheduleAndTimeRecords().add(Tuples.of(theoreticalDay, employeeTimeRecord));
    }

    private static TheoreticalTimesheetDay getTheoreticalDay(Instant date, Timesheet timesheet, ScheduleStatus status) {
        TheoreticalTimesheetDay theoreticalDay;
        if (ScheduleStatus.NORM.equals(status) && timesheet != null) {
            theoreticalDay = new TheoreticalTimesheetDay(
                    date, timesheet.getId(), timesheet.getEntryTime(),
                    timesheet.getBreakDepartureTime(), timesheet.getBreakReturnTime(),
                    timesheet.getDepartureTime(), status.toString());
        } else {
            theoreticalDay = new TheoreticalTimesheetDay(
                    date, null, null, null,
                    null, null, status.toString());
        }
        return theoreticalDay;
    }
}
