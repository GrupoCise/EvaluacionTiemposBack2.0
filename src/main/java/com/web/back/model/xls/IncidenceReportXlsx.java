package com.web.back.model.xls;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class IncidenceReportXlsx {
    private String incidenceDescription;
    private String idRetorno;
    private List<EmployeeIncidenceXlsx> employeeIncidenceList;
}

