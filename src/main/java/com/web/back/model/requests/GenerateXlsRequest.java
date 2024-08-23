package com.web.back.model.requests;

import com.web.back.model.responses.evaluacion.Employee;
import com.web.back.model.responses.evaluacion.Incidencia;

import java.util.List;

public record GenerateXlsRequest(String beginDate, String endDate, String sociedad, String areaNomina, List<Incidencia> incidences, List<Employee> extraEmployeesData) {
}
