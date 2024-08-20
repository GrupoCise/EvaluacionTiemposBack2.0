package com.web.back.model.dto;

import com.web.back.model.responses.evaluacion.Employee;

import java.util.List;

public record GetEmployeesRequestDto(String beginDate, String endDate, String sociedad, String areaNomina, List<Employee> extraEmployeesData) {
}
