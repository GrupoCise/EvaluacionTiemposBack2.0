package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.mappers.*;
import com.web.back.model.dto.CatIncidenceEmployeeDto;
import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.dto.EvaluationsDataDto;
import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.entities.*;
import com.web.back.model.requests.RegistroTiemposRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Employee;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import com.web.back.model.responses.evaluacion.Incidencia;
import com.web.back.repositories.CatIncidenceEmployeeRepository;
import com.web.back.repositories.CatIncidenceRepository;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.utils.DateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;
    private final EvaluationRepository evaluationRepository;
    private final CatIncidenceRepository catIncidenceRepository;
    private final CatIncidenceEmployeeRepository catIncidenceEmployeeRepository;
    private final FiltersService filtersService;

    public EmployeeService(ZWSHREvaluacioClient zwshrEvaluacioClient, EvaluationRepository evaluationRepository, CatIncidenceRepository catIncidenceRepository, CatIncidenceEmployeeRepository catIncidenceEmployeeRepository, FiltersService filtersService) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
        this.evaluationRepository = evaluationRepository;
        this.catIncidenceRepository = catIncidenceRepository;
        this.catIncidenceEmployeeRepository = catIncidenceEmployeeRepository;
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

    public CustomResponse<EvaluationsDataDto> getEmployeesByFilters(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        var savedEmployeesOnEvaluation = evaluationRepository.findByFechaAndAreaNominaAndSociedad(beginDate, endDate, sociedad, areaNomina);

        if (!savedEmployeesOnEvaluation.isEmpty()) {
            var incidencesEmployees = catIncidenceEmployeeRepository.findAllByEmployeeNums(savedEmployeesOnEvaluation.stream().map(Evaluation::getNumEmpleado).distinct().toList()).stream()
                    .collect(Collectors.groupingBy(CatIncidenceEmployee::getId, Collectors.mapping(CatIncidenceEmployee::getCatIncidence, Collectors.toList())))
                    .entrySet().stream()
                    .map(e -> new CatIncidenceEmployeeDto(e.getKey().getEmployeeNum(), e.getValue().stream().map(CatIncidenceDtoMapper::map).toList()))
                    .toList();

            return new CustomResponse<EvaluationsDataDto>().ok(
                    new EvaluationsDataDto(savedEmployeesOnEvaluation.stream().map(EvaluationDtoMapper::mapFrom).toList(),
                            incidencesEmployees)
            );
        }

        return getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, userName);
    }

    public CustomResponse<EvaluationsDataDto> getEmployeesByFiltersFromService(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());

        var enrichedEmployeesDataMap = getEnrichedEmployeesDataMap(evaluations);

        evaluations.forEach(employee -> processEmployee(evaluationDtos, employee, enrichedEmployeesDataMap, sociedad, areaNomina));

        List<CatIncidenceEmployee> incidencesEmployee = persistIncidencesCatalog(enrichedEmployeesDataMap);

        var incidencesEmployees = incidencesEmployee.stream()
                .collect(Collectors.groupingBy(CatIncidenceEmployee::getId, Collectors.mapping(CatIncidenceEmployee::getCatIncidence, Collectors.toList())))
                .entrySet().stream()
                .map(e -> new CatIncidenceEmployeeDto(e.getKey().getEmployeeNum(), e.getValue().stream().map(CatIncidenceDtoMapper::map).toList()))
                .toList();

        return new CustomResponse<EvaluationsDataDto>().ok(new EvaluationsDataDto(evaluationDtos, incidencesEmployees));
    }

    private void processEmployee(List<EvaluationDto> evaluationDtos, EmployeeApiResponse employee,
                                 Map<String, EvaluacionApiResponse> enrichedEmployeesDataMap, String sociedad, String areaNomina) {
        var employeeData = getEmployeeData(enrichedEmployeesDataMap, employee);

        var optionalEmployee = evaluationRepository.findByFechaAndHorarioAndTurnAndAreaNominaAndSociedadAndEmpleado(
                DateUtil.toStringYYYYMMDD(employee.getFecha()), employee.getHorario(), employee.getTurno(), sociedad, areaNomina, employee.getEmpleado());

        if (optionalEmployee.isPresent()) {
            updateExistingEmployee(evaluationDtos, employeeData, optionalEmployee.get());
        } else {
            saveNewEmployee(evaluationDtos, employee, employeeData, sociedad, areaNomina);
        }
    }

    private void updateExistingEmployee(List<EvaluationDto> evaluationDtos, Employee employeeData, Evaluation employee) {
        if (employeeData != null) {
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

    private Employee getEmployeeData(Map<String, EvaluacionApiResponse> enrichedEmployeesDataMap, EmployeeApiResponse employee) {
        var filtersResponse = enrichedEmployeesDataMap.get(employee.getEmpleado());
        if (filtersResponse != null && !filtersResponse.getEmpleados().isEmpty()) {
            var employeeData = filtersResponse.getEmpleados().stream().filter(f -> f.getPernr().equals(employee.getEmpleado())).findFirst();
            if (employeeData.isPresent()) {
                return employeeData.get();
            }
        }
        return null;
    }

    private Map<String, EvaluacionApiResponse> getEnrichedEmployeesDataMap(List<EmployeeApiResponse> evaluations) {
        Map<String, EvaluacionApiResponse> map = new HashMap<>();
        for (String employeeNumber : evaluations.stream().map(EmployeeApiResponse::getEmpleado).distinct().toList()) {
            map.put(employeeNumber, Optional.ofNullable(filtersService.getFilters(employeeNumber).block()).orElse(new EvaluacionApiResponse()));
        }
        return map;
    }

    private List<CatIncidenceEmployee> persistIncidencesCatalog(Map<String, EvaluacionApiResponse> enrichedEmployeesDataMap) {
        List<CatIncidenceEmployee> incidencesEmployee = new ArrayList<>();

        var incidences = enrichedEmployeesDataMap.values().stream()
                .map(EvaluacionApiResponse::getCatIncidencias)
                .flatMap(Collection::stream)
                .distinct()
                .toList();

        var existentIncidences = catIncidenceRepository.findAllByIdReglasAndIdRetornosAndMandt(
                incidences.stream().map(Incidencia::getIdRegla).toList(),
                incidences.stream().map(Incidencia::getIdRetorno).toList(),
                incidences.stream().map(Incidencia::getMandt).toList()
        );

        var newIncidences = incidences.stream()
                .filter(i -> existentIncidences.stream().noneMatch(c ->
                        c.getIdRegla().equals(i.getIdRegla()) &&
                                c.getIdRetorno().equals(i.getIdRetorno()) &&
                                c.getMandt().equals(i.getMandt())
                ))
                .map(IncidenciaToCatIncidenceMapper::map)
                .toList();

        catIncidenceRepository.saveAll(newIncidences);

        enrichedEmployeesDataMap.forEach((employeeNumber, evaluacionApiResponse) -> {
            for (Incidencia incidencia : evaluacionApiResponse.getCatIncidencias()) {
                var incidence = existentIncidences.stream().filter(c ->
                        c.getIdRegla().equals(incidencia.getIdRegla()) &&
                                c.getIdRetorno().equals(incidencia.getIdRetorno()) &&
                                c.getMandt().equals(incidencia.getMandt())
                ).findFirst().or(() -> newIncidences.stream().filter(c ->
                        c.getIdRegla().equals(incidencia.getIdRegla()) &&
                                c.getIdRetorno().equals(incidencia.getIdRetorno()) &&
                                c.getMandt().equals(incidencia.getMandt())
                ).findFirst());

                if (incidence.isEmpty()) {
                    throw new RuntimeException(String.format("Error al persisitir la incidencia %s", incidencia.getIdRegla()));
                }

                CatIncidenceEmployeeId catIncidenceEmployeeId = new CatIncidenceEmployeeId();
                catIncidenceEmployeeId.setEmployeeNum(employeeNumber);
                catIncidenceEmployeeId.setCatIncidenceId(incidence.get().getId());

                CatIncidenceEmployee catIncidenceEmployee = catIncidenceEmployeeRepository.findById(catIncidenceEmployeeId)
                        .orElseGet(() -> {
                            CatIncidenceEmployee newCatIncidenceEmployee = new CatIncidenceEmployee();
                            newCatIncidenceEmployee.setId(catIncidenceEmployeeId);
                            return newCatIncidenceEmployee;
                        });

                catIncidenceEmployee.setCatIncidence(incidence.get());

                catIncidenceEmployeeRepository.save(catIncidenceEmployee);

                incidencesEmployee.add(catIncidenceEmployee);
            }
        });

        return incidencesEmployee;
    }
}
