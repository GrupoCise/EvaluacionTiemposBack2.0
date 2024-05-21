package com.web.back.builders;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.entities.User;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChangeLogBuilder {
    private ChangeLogBuilder() {
    }

    public static List<ChangeLog> buildFrom(Evaluation original, EvaluationDto updated, User user) {
        var updateDate = Instant.now();

        return Stream.of(
                        build("Horas Tomadas", original.getHorasTomadas(), updated.getHorasTomadas(), original, user.getId(), updateDate),
                        build("Horas Extras", original.getHorasExtra(), updated.getHorasExtra(), original, user.getId(), updateDate),
                        build("Horario", original.getHorario(), updated.getHorario(), original, user.getId(), updateDate),
                        build("Hora Entrada", original.getHoraEntrada(), updated.getHoraEntrada(), original, user.getId(), updateDate),
                        build("Hora Salida", original.getHoraSalida(), updated.getHoraSalida(), original, user.getId(), updateDate),
                        build("Resultado General", original.getResultadoGeneral(), updated.getResultadoGeneral(), original, user.getId(), updateDate),
                        build("Comentario", original.getComentario(), updated.getComentario(), original, user.getId(), updateDate),
                        build("Enlace", original.getEnlace(), updated.getEnlace(), original, user.getId(), updateDate)
                ).filter(changeLog -> !changeLog.getOriginal().equals(changeLog.getUpdated()))
                .collect(Collectors.toList());
    }

    private static ChangeLog build(String field,
                                   Object originalValue,
                                   Object updatedValue,
                                   Evaluation baseEvaluation,
                                   Integer userId,
                                   Instant updateDate) {
        ChangeLog changeLog = new ChangeLog();

        changeLog.setField(field);
        changeLog.setOriginal(originalValue.toString());
        changeLog.setUpdated(updatedValue.toString());
        changeLog.setNumEmpleado(baseEvaluation.getNumEmpleado());
        changeLog.setEvaluationId(baseEvaluation.getId());
        changeLog.setUserId(userId);
        changeLog.setSociedad(baseEvaluation.getSociedad());
        changeLog.setAreaNomina(baseEvaluation.getAreaNomina());
        changeLog.setUpdatedOn(updateDate);

        return changeLog;
    }
}
