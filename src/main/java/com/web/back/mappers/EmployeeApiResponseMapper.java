package com.web.back.mappers;

import com.web.back.model.entities.Evaluation;
import com.web.back.model.responses.EmployeeApiResponse;

public final class EmployeeApiResponseMapper {
    private EmployeeApiResponseMapper() {}

    public static EmployeeApiResponse map(Evaluation employee) {
        return new EmployeeApiResponse(employee.getNumEmpleado(),
                employee.getFecha(),
                employee.getHoraEntrada(),
                employee.getHoraPausa(),
                employee.getHoraRegresoPausa(),
                employee.getHoraSalida(),
                employee.getResultadoEntrada(),
                employee.getResultadoPausa(),
                employee.getResultadoRegresoPausa(),
                employee.getResultadoSalida(),
                employee.getStatusRegistro(),
                employee.getHorario(),
                employee.getHorasExtra(),
                employee.getTipoHrsExtra());
    }
}
