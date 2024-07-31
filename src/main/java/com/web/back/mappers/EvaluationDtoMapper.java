package com.web.back.mappers;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.Evaluation;

public final class EvaluationDtoMapper {
    private EvaluationDtoMapper() {}

    public static EvaluationDto mapFrom(Evaluation evaluation){
        return new EvaluationDto(evaluation.getId(),
                evaluation.getFecha(),
                evaluation.getHoraEntrada(),
                evaluation.getHoraPausa(),
                evaluation.getHoraRegresoPausa(),
                evaluation.getHoraSalida(),
                evaluation.getResultadoEntrada(),
                evaluation.getResultadoPausa(),
                evaluation.getResultadoRegresoPausa(),
                evaluation.getResultadoSalida(),
                evaluation.getResultadoGeneral(),
                evaluation.getStatusRegistro(),
                evaluation.getNumEmpleado(),
                "",
                evaluation.getHorario(),
                evaluation.getComentario(),
                evaluation.getEnlace(),
                evaluation.getHorasExtra(),
                evaluation.getHorasTomadas(),
                evaluation.getAreaNomina(),
                evaluation.getSociedad(),
                evaluation.getTipoHrsExtra(),
                evaluation.getTipoIncidencia(),
                evaluation.getReferencia(),
                evaluation.getConsecutivo1(),
                evaluation.getConsecutivo2(),
                evaluation.getApprobationLevel(),
                evaluation.getTurn());
    }
}
