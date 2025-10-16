package com.web.back.process;

import com.web.back.model.entities.*;
import com.web.back.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluationAtLevelTwoProcess {
    private final TimeRuleRepository timeRuleRepository;
    private final TimesheetTimeRuleRepository timesheetTimeRuleRepository;
    private final EmployeeTimesheetsRepository employeeTimesheetsRepository;
    private final EvaluationRepository evaluationRepository;
    private final EmployeeRepository employeeRepository;

    public EvaluationAtLevelTwoProcess(TimeRuleRepository timeRuleRepository,
                                       TimesheetTimeRuleRepository timesheetTimeRuleRepository,
                                       EmployeeTimesheetsRepository employeeTimesheetsRepository,
                                       EvaluationRepository evaluationRepository,
                                       EmployeeRepository employeeRepository) {
        this.timeRuleRepository = timeRuleRepository;
        this.timesheetTimeRuleRepository = timesheetTimeRuleRepository;
        this.employeeTimesheetsRepository = employeeTimesheetsRepository;
        this.evaluationRepository = evaluationRepository;
        this.employeeRepository = employeeRepository;
    }

    public void executeLevelTwoRules(Instant beginDate, Instant endDate) {
        var evaluationsToUpdate = new ArrayList<Evaluation>();
        var evaluations = evaluationRepository.findAllByFechaBetween(
                LocalDate.from(beginDate), LocalDate.from(endDate));

        var groupedByEmployee = evaluations.stream()
                .collect(Collectors.groupingBy(Evaluation::getNumEmpleado));

        var employeeNumbers = groupedByEmployee.keySet();

        var employees = employeeRepository.findAllByNumEmployeeIn(employeeNumbers);

        var employeeIds = employees.stream()
                .map(Employee::getId)
                .toList();

        List<EmployeeTimesheet> timeSheetEmployees = employeeTimesheetsRepository.findOverlappingTimesheetsByIds(
                employeeIds, beginDate, endDate);

        var timeSheetIds = timeSheetEmployees.stream()
                .map(tse -> tse.getTimesheet().getId())
                .distinct()
                .toList();

        List<TimesheetTimeRule> timesheetTimeRules = timesheetTimeRuleRepository.findAllByTimesheetIdIn(timeSheetIds);

        var timeRuleIds = timesheetTimeRules.stream()
                .map(tr -> tr.getTimeRule().getId())
                .distinct()
                .toList();

        List<TimeRule> timeRuleEntities = timeRuleRepository.findAllById(timeRuleIds);

        var levelTwoRules = timeRuleEntities.stream()
                .filter(tr -> tr.getLevel() == 2)
                .sorted(Comparator.comparing(TimeRule::getSequence))
                .toList();

        groupedByEmployee.forEach((employeeNumber, employeeEvaluations) ->{
            var employee = employees.stream()
                    .filter(emp -> emp.getNumEmployee().equals(employeeNumber))
                    .findFirst()
                    .orElse(null);

            if (employee == null) return;

            for(var rule : levelTwoRules) {
                String ruleStr = rule.getRule();
                Pattern pattern = Pattern.compile("SUM\\((\\w+)\\)\\s*(==|=|>=|<=|>|<)\\s*(\\d+)");
                Matcher matcher = pattern.matcher(ruleStr);

                if (!matcher.find()) continue;

                List<String> fieldsToSum = Arrays.asList(matcher.group(1).split("\\|"));
                String operator = matcher.group(2);
                int targetCount = Integer.parseInt(matcher.group(3));

                int count = 0;

                for (Evaluation evaluation : employeeEvaluations.stream()
                        .sorted(Comparator.comparing(Evaluation::getFecha))
                        .toList()) {

                    var timeSheetForDate = timeSheetEmployees.stream()
                            .filter(tse ->
                                    tse.getEmployee().getId().equals(employee.getId()) &&
                                            !tse.getFromDate().isAfter(Instant.from(evaluation.getFecha())) &&
                                            !tse.getToDate().isBefore(Instant.from(evaluation.getFecha()))
                            )
                            .findFirst()
                            .orElse(null);

                    if (timeSheetForDate == null) return;

                    var ruleAppliesForCurrentTimesheet = timesheetTimeRules.stream()
                            .anyMatch(ttr ->
                                    ttr.getTimesheet().getId().equals(timeSheetForDate.getTimesheet().getId()) &&
                                            ttr.getTimeRule().getId().equals(rule.getId()));

                    if (!ruleAppliesForCurrentTimesheet) return;

                    List<String> resultFields = Arrays.asList(evaluation.getResultadoGeneral().split("\\|"));
                    if (fieldsToSum.stream().anyMatch(resultFields::contains)) {
                        count++;
                        if (matchesCondition(count, operator, targetCount)) {
                            evaluation.setResultadoGeneral(rule.getRule());// In level two rules the general result is overridden not appended

                            int idx = evaluationsToUpdate.indexOf(evaluation);
                            if (idx >= 0) {
                                evaluationsToUpdate.set(idx, evaluation); // Replace to keep the latest general result value
                            } else {
                                evaluationsToUpdate.add(evaluation); // Add
                            }

                            count = 0;
                        }
                    }
                }
            }
        });

        if (evaluationsToUpdate.isEmpty()) return;

        evaluationRepository.saveAll(evaluationsToUpdate);
    }

    private boolean matchesCondition(int count, String operator, int target) {
        return switch (operator) {
            case "==", "=" -> count == target;
            case ">"      -> count > target;
            case "<"      -> count < target;
            case ">="     -> count >= target;
            case "<="     -> count <= target;
            default       -> false;
        };
    }
}
