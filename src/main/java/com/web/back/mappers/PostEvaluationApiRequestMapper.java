package com.web.back.mappers;

import com.web.back.model.entities.Evaluation;
import com.web.back.model.requests.PostEvaluationApiRequest;
import com.web.back.utils.DateUtil;

public final class PostEvaluationApiRequestMapper {
    private PostEvaluationApiRequestMapper() {}

    public static PostEvaluationApiRequest mapFrom(Evaluation evaluation){
        return new PostEvaluationApiRequest(evaluation.getNumEmpleado(),
                DateUtil.toStringYYYYMMDD(evaluation.getFecha()),
                evaluation.getHoraEntrada() != null ? DateUtil.timeToString(evaluation.getHoraEntrada()) : null,
                evaluation.getHoraSalida() != null ? DateUtil.timeToString(evaluation.getHoraSalida()) : null,
                evaluation.getResultadoGeneral(),
                evaluation.getHorario(),
                evaluation.getTurn() != null ? evaluation.getTurn().toString() : null,
                evaluation.getEnlace(),
                evaluation.getConsecutivo1(),
                evaluation.getConsecutivo2());
    }
}
