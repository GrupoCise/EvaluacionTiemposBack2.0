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
import com.web.back.model.responses.evaluacion.Incidencia;
import com.web.back.repositories.CatIncidenceEmployeeRepository;
import com.web.back.repositories.CatIncidenceRepository;
import com.web.back.repositories.EvaluationRepository;
import com.web.back.utils.DateUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

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
            var filters = Objects.requireNonNull(filtersService.getFilters(userName).block());

            var incidences = persistIncidencesCatalog(List.of(), filters, sociedad, areaNomina);

            var incidencesEmployees = catIncidenceRepository.findAllByIdSociedadAndAreaNomina(sociedad, areaNomina);

            var catIncidences = Stream.of(incidences, incidencesEmployees)
                    .flatMap(Collection::stream).distinct()
                    .map(CatIncidenceDtoMapper::map)
                    .toList();

            return new CustomResponse<EvaluationsDataDto>().ok(
                    new EvaluationsDataDto(savedEmployeesOnEvaluation.stream().map(EvaluationDtoMapper::mapFrom).toList(), catIncidences)
            );
        }

        return getEmployeesByFiltersFromService(beginDate, endDate, sociedad, areaNomina, userName);
    }

    public CustomResponse<EvaluationsDataDto> getEmployeesByFiltersFromService(String beginDate, String endDate, String sociedad, String areaNomina, String userName) {
        List<EvaluationDto> evaluationDtos = new ArrayList<>();

        var evaluations = Objects.requireNonNull(zwshrEvaluacioClient.getEmployees(userName, beginDate, endDate, sociedad, areaNomina).block());
        var filters = Objects.requireNonNull(filtersService.getFilters(userName).block());

        evaluations.forEach(employee -> processEmployee(evaluationDtos, employee, filters, sociedad, areaNomina));

        List<CatIncidence> incidences = persistIncidencesCatalog(evaluations.stream().map(EmployeeApiResponse::getEmpleado).toList(), filters, sociedad, areaNomina);

        return new CustomResponse<EvaluationsDataDto>().ok(new EvaluationsDataDto(evaluationDtos, incidences.stream().map(CatIncidenceDtoMapper::map).toList()));
    }

    private void processEmployee(List<EvaluationDto> evaluationDtos, EmployeeApiResponse employee,
                                 EvaluacionApiResponse filters, String sociedad, String areaNomina) {
        var employeeData = getEmployeeData(filters, employee);

        var optionalEmployee = evaluationRepository.findByFechaAndHorarioAndTurnAndAreaNominaAndSociedadAndEmpleado(
                DateUtil.toStringYYYYMMDD(employee.getFecha()), employee.getHorario(), employee.getTurno(), sociedad, areaNomina, employee.getEmpleado());

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

    private Employee getEmployeeData(EvaluacionApiResponse filters, EmployeeApiResponse employee) {
        var employeeData = filters.getEmpleados().stream().filter(f -> f.getPernr().equals(employee.getEmpleado())).findFirst();
        return employeeData.orElse(null);
    }

    private List<CatIncidence> persistIncidencesCatalog(List<String> employees, EvaluacionApiResponse filters, String sociedad, String areaNomina) {
        List<CatIncidenceEmployee> incidencesEmployee = new ArrayList<>();

        var incidences = filters.getCatIncidencias();

        var existentIncidences = catIncidenceRepository.findAllByIdSociedadAndAreaNomina(sociedad, areaNomina);

        var newIncidences = incidences.stream()
                .filter(i -> existentIncidences.stream().noneMatch(c ->
                                c.getIdRegla().equals(i.getIdRegla()) &&
                                        c.getIdRetorno().equals(i.getIdRetorno()) &&
                                        c.getMandt().equals(i.getMandt()) &&
                                        c.getSociedad().equals(sociedad) &&
                                        c.getAreaNomina().equals(areaNomina)
                        )
                )
                .map(m -> IncidenciaToCatIncidenceMapper.map(m, sociedad, areaNomina))
                .toList();

        if (!newIncidences.isEmpty()) {
            catIncidenceRepository.saveAll(newIncidences);
        }

        var catIncidences = Stream.of(existentIncidences, newIncidences)
                .flatMap(Collection::stream).toList();

        var incidencesEmployees = catIncidenceEmployeeRepository.findAllByEmployeeNums(employees);

        for (String employeeNumber : employees) {
            for (Incidencia incidencia : incidences) {
                var incidence = catIncidences.stream()
                        .filter(c ->
                                c.getIdRegla().equals(incidencia.getIdRegla()) &&
                                        c.getIdRetorno().equals(incidencia.getIdRetorno()) &&
                                        c.getMandt().equals(incidencia.getMandt()) &&
                                        c.getSociedad().equals(sociedad) &&
                                        c.getAreaNomina().equals(areaNomina)
                        ).findFirst();

                if (incidence.isEmpty()) {
                    throw new RuntimeException(String.format("Error al persisitir la incidencia %s", incidencia.getIdRegla()));
                }

                CatIncidenceEmployeeId catIncidenceEmployeeId = new CatIncidenceEmployeeId();
                catIncidenceEmployeeId.setEmployeeNum(employeeNumber);
                catIncidenceEmployeeId.setCatIncidenceId(incidence.get().getId());

                CatIncidenceEmployee catIncidenceEmployee = incidencesEmployees.stream().filter(f -> f.getId().equals(catIncidenceEmployeeId)).findFirst()
                        .orElseGet(() -> {
                            CatIncidenceEmployee newCatIncidenceEmployee = new CatIncidenceEmployee();
                            newCatIncidenceEmployee.setId(catIncidenceEmployeeId);
                            return newCatIncidenceEmployee;
                        });

                catIncidenceEmployee.setCatIncidence(incidence.get());

                incidencesEmployee.add(catIncidenceEmployee);
            }
        }

        if (!incidencesEmployee.isEmpty()) {
            catIncidenceEmployeeRepository.saveAll(incidencesEmployee);
        }

        return catIncidences;
    }
}
