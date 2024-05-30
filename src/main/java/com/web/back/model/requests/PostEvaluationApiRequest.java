package com.web.back.model.requests;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostEvaluationApiRequest(
        String empleado,
        String fecha,
        String horaEntrada,
        String horaSalida,
        String estatusGen,
        String horario,
        String enlace,
        String consecutivo1,
        String consecutivo2
) {
}
