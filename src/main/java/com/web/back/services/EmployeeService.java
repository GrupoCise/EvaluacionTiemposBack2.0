package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.*;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.dto.EvaluationsDataDto;
import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.entities.*;
import com.web.back.model.requests.GenerateXlsRequest;
import com.web.back.model.requests.RegistroTiemposRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Employee;
import com.web.back.model.xls.EmployeeIncidenceXlsx;
import com.web.back.model.xls.interfaces.XlsxWriter;
import com.web.back.repositories.EvaluationRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final EvaluationRepository evaluationRepository;
    private final XlsxWriter xlsxWriter;

    public EmployeeService(ZWSHREvaluacioClient zwshrEvaluacioClient, EvaluationRepository evaluationRepository, FiltersService filtersService, XlsxWriter xlsxWriter) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.evaluationRepository = evaluationRepository;
        this.xlsxWriter = xlsxWriter;
    }

    public CustomResponse<List<RegistroTiemposDto>> getRegistroTiempos(String beginDate, String endDate, String userName) {
        List<RegistroTiemposDto> registroTiemposList = zwshrEvaluacioClient.getRegistroTiempos(userName, beginDate, endDate)
                .map(RegistroTiemposDtoMapper::mapFrom)
                .block();

        return new CustomResponse<List<RegistroTiemposDto>>().ok(registroTiemposList);
    }

    public ResponseEntity<Void> sendTimeSheetChanges(List<RegistroTiemposDto> registroTiemposDtos) {
        List<RegistroTiemposRequest> request = RegistroTiemposDtoMapper.mapToRequestFrom(registroTiemposDtos);

        return zwshrEvaluacioClient.postRegistroTiempos(request).block();
    }

    @Transactional
    public CustomResponse<EvaluationsDataDto> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName, List<Employee> extraEmployeesData) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();
        var savedEmployeesOnEvaluation = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);
        var employeesNumbers = extraEmployeesData.stream().map(Employee::getPernr).toList();

        if (!savedEmployeesOnEvaluation.isEmpty()) {

            var employeesForAuthUser = savedEmployeesOnEvaluation.stream().filter(e -> employeesNumbers.contains(e.getNumEmpleado())).toList();
            var employeesNumbersForAuthUser = employeesForAuthUser.stream().map(Evaluation::getNumEmpleado).toList();
            var missingEmployees = employeesNumbers.stream().filter(eNum -> !employeesNumbersForAuthUser.contains(eNum)).toList();

            if(!missingEmployees.isEmpty()) {
                var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());
                evaluations.forEach(employee -> {
                    if(missingEmployees.contains(employee.getEmpleado())) {
                        processEmployee(evaluationDtos, savedEmployeesOnEvaluation, employee, extraEmployeesData, sociedad, areaNomina);
                    }
                });
            }

            evaluationDtos.addAll(employeesForAuthUser.stream().map(EvaluationDtoMapper::mapFrom).toList());
        }

        if(evaluationDtos.isEmpty()){
            var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());

            evaluations.forEach(employee -> {
                if(employeesNumbers.contains(employee.getEmpleado())) {
                    processEmployee(evaluationDtos, savedEmployeesOnEvaluation, employee, extraEmployeesData, sociedad, areaNomina);
                }
            });
        }

        return new CustomResponse<EvaluationsDataDto>().ok(new EvaluationsDataDto(evaluationDtos));
    }

    @Transactional
    public CustomResponse<EvaluationsDataDto> getEmployeesCleanSync(String beginDate, String endDate, String sociedad, String areaNomina, String userName, List<Employee> extraEmployeesData) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());

        var existentEmployees = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        evaluations.forEach(employee -> processEmployee(evaluationDtos, existentEmployees, employee, extraEmployeesData, sociedad, areaNomina));

        var employeesNumbers = extraEmployeesData.stream().map(Employee::getPernr).toList();

        evaluationDtos.removeIf(e -> !employeesNumbers.contains(e.getNumEmpleado()));

        return new CustomResponse<EvaluationsDataDto>().ok(new EvaluationsDataDto(evaluationDtos));
    }

    @Transactional
    public byte[] getLogsXlsData(GenerateXlsRequest request) {
        var evaluations = evaluationRepository.findByFechaAndAreaNominaAndSociedad(request.beginDate(), request.endDate(), request.sociedad(), request.areaNomina());

        var employeesNumbers = request.extraEmployeesData().stream().map(Employee::getPernr).toList();

        evaluations.removeIf(e -> !employeesNumbers.contains(e.getNumEmpleado()));

        var evaluationXlsxes = IncidenceReportXlsxMapper.mapFrom(request.incidences(), evaluations);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos; Workbook workbook = new XSSFWorkbook()) {
            String[] columnTitles = EmployeeIncidenceXlsx.getColumnTitles();

            evaluationXlsxes.forEach(e -> {
                xlsxWriter.appendSheet(e.getEmployeeIncidenceList(), bos, columnTitles, workbook,
                        String.format("%1$s - %2$s", e.getIdRetorno(), e.getIncidenceDescription()));
            });

            workbook.write(bos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

    private void processEmployee(List<EvaluationDto> evaluationDtos, List<Evaluation> existentEmployees, EmployeeApiResponse employee,
                                 List<Employee> extraEmployeesData, String sociedad, String areaNomina) {
        var employeeData = getEmployeeData(extraEmployeesData, employee);

        var optionalEmployee = existentEmployees.stream().filter(f ->
                f.getNumEmpleado().equals(employee.getEmpleado()) &&
                        f.getFecha().equals(employee.getFecha()) &&
                        Objects.equals(f.getTurn(), employee.getTurno())
        ).findFirst();

        if (optionalEmployee.isPresent()) {
            updateExistingEmployee(evaluationDtos, employeeData, optionalEmployee.get());
        } else {
            saveNewEmployee(evaluationDtos, employee, employeeData, sociedad, areaNomina);
        }
    }

    private void updateExistingEmployee(List<EvaluationDto> evaluationDtos, Employee employeeData, Evaluation employee) {
        if (employeeData != null &&
                !employeeData.getVdsk1().equals(employee.getPayroll()) &&
                !employeeData.getNombre().equals(employee.getEmployeeName())) {
            employee.setEmployeeName(employee.getEmployeeName());
            employee.setPayroll(employeeData.getVdsk1());
            evaluationRepository.save(employee);
        }
        evaluationDtos.add(EvaluationDtoMapper.mapFrom(employee));
    }

    private void saveNewEmployee(List<EvaluationDto> evaluationDtos, EmployeeApiResponse employee, Employee employeeData, String sociedad, String areaNomina) {
        var evaluation = EvaluationMapper.mapFrom(employee, employeeData, sociedad, areaNomina);
        evaluationRepository.save(evaluation);
        evaluationDtos.add(EvaluationDtoMapper.mapFrom(evaluation));
    }

    private Employee getEmployeeData(List<Employee> extraEmployeesData, EmployeeApiResponse employee) {
        var employeeData = extraEmployeesData.stream().filter(f -> f.getPernr().equals(employee.getEmpleado())).findFirst();
        return employeeData.orElse(null);
    }
}
