package com.web.back.mappers;

import com.web.back.model.xls.EmployeeIncidenceXlsx;
import com.web.back.model.xls.IncidenceReportXlsx;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.responses.evaluacion.Incidencia;
import com.web.back.utils.DateUtil;

import java.util.Comparator;
import java.util.List;

public final class IncidenceReportXlsxMapper {
    private IncidenceReportXlsxMapper(){}

    public static List<IncidenceReportXlsx> mapFrom(List<Incidencia> incidences, List<Evaluation> evaluations){
        Comparator<EmployeeIncidenceXlsx> comparator = Comparator.comparing(EmployeeIncidenceXlsx::getEmployeeNumber)
                .thenComparing(EmployeeIncidenceXlsx::getFecha);

        return incidences.stream().map(incidence -> {
            List<EmployeeIncidenceXlsx> employeeIncidenceList = evaluations.stream()
                    .filter(evaluation -> evaluation.getResultadoGeneral().contains(incidence.getIdRetorno()))
                    .map(evaluation -> new EmployeeIncidenceXlsx(
                            evaluation.getNumEmpleado(),
                            evaluation.getEmployeeName(),
                            evaluation.getFecha() != null ? DateUtil.toStringYYYYMMDD(evaluation.getFecha()) : ""))
                    .sorted(comparator).toList();

            return new IncidenceReportXlsx(incidence.getDescripcion(), incidence.getIdRetorno(), employeeIncidenceList);
        }).toList();

    }

}
