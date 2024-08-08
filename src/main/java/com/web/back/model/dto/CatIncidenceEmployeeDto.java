package com.web.back.model.dto;

import com.web.back.model.entities.CatIncidence;

import java.util.List;

public record CatIncidenceEmployeeDto(String employeeNumber, List<CatIncidenceDto> incidences) {
}
