package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.*;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.dto.EvaluationsDataDto;
import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.entities.*;
import com.web.back.model.requests.RegistroTiemposRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Employee;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.utils.DateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final EvaluationRepository evaluationRepository;
    private final FiltersService filtersService;

    public EmployeeService(ZWSHREvaluacioClient zwshrEvaluacioClient, EvaluationRepository evaluationRepository, FiltersService filtersService) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.evaluationRepository = evaluationRepository;
        this.filtersService = filtersService;
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

    public CustomResponse<EvaluationsDataDto> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName, List<Employee> extraEmployeesData) {
        var savedEmployeesOnEvaluation = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        if (!savedEmployeesOnEvaluation.isEmpty()) {
            var employeesNumbers = extraEmployeesData.stream().map(Employee::getPernr).toList();

            savedEmployeesOnEvaluation.removeIf(e -> !employeesNumbers.contains(e.getNumEmpleado()));

            return new CustomResponse<EvaluationsDataDto>().ok(
                    new EvaluationsDataDto(savedEmployeesOnEvaluation.stream().map(EvaluationDtoMapper::mapFrom).toList())
            );
        }

        return getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, userName, extraEmployeesData);
    }

    public CustomResponse<EvaluationsDataDto> getEmployeesByFiltersFromService(String beginDate, String endDate, String sociedad, String areaNomina, String userName, List<Employee> extraEmployeesData) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());

        var existentEmployees = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        evaluations.forEach(employee -> processEmployee(evaluationDtos, existentEmployees, employee, extraEmployeesData, sociedad, areaNomina));

        var employeesNumbers = extraEmployeesData.stream().map(Employee::getPernr).toList();

        evaluationDtos.removeIf(e -> !employeesNumbers.contains(e.getNumEmpleado()));

        return new CustomResponse<EvaluationsDataDto>().ok(new EvaluationsDataDto(evaluationDtos));
    }

    private void processEmployee(List<EvaluationDto> evaluationDtos, List<Evaluation> existentEmployees, EmployeeApiResponse employee,
                                 List<Employee> extraEmployeesData, String sociedad, String areaNomina) {
        var employeeData = getEmployeeData(extraEmployeesData, employee);

        var optionalEmployee = existentEmployees.stream().filter(f ->
                f.getNumEmpleado().equals(employee.getEmpleado()) &&
                f.getFecha().equals(employee.getFecha()) &&
                f.getHorario().equals(employee.getHorario()) &&
                f.getTurn().equals(employee.getTurno()) &&
                f.getSociedad().equals(sociedad) &&
                f.getAreaNomina().equals(areaNomina)
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
