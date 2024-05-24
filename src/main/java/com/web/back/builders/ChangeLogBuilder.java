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
                        build("Horas Tomadas", original.getHorasTomadas(), updated.getHorasTomadas(), updated, original, user, updateDate),
                        build("Horas Extras", original.getHorasExtra(), updated.getHorasExtra(), updated, original, user, updateDate),
                        build("Horario", original.getHorario(), updated.getHorario(), updated, original, user, updateDate),
                        build("Hora Entrada", original.getHoraEntrada(), updated.getHoraEntrada(), updated, original, user, updateDate),
                        build("Hora Salida", original.getHoraSalida(), updated.getHoraSalida(), updated, original, user, updateDate),
                        build("Resultado General", original.getResultadoGeneral(), updated.getResultadoGeneral(), updated, original, user, updateDate),
                        build("Comentario", original.getComentario(), updated.getComentario(), updated, original, user, updateDate),
                        build("Enlace", original.getEnlace(), updated.getEnlace(), updated, original, user, updateDate)
                ).filter(changeLog -> !changeLog.getOriginal().equals(changeLog.getUpdated()))
                .collect(Collectors.toList());
    }

    private static ChangeLog build(String field,
                                   Object originalValue,
                                   Object updatedValue,
                                   EvaluationDto updated,
                                   Evaluation baseEvaluation,
                                   User user,
                                   Instant updateDate) {
        ChangeLog changeLog = new ChangeLog();

        changeLog.setField(field);
        changeLog.setOriginal(originalValue.toString());
        changeLog.setUpdated(updatedValue.toString());
        changeLog.setNumEmpleado(baseEvaluation.getNumEmpleado());
        changeLog.setEmpleadoName(updated.getEmpleadoName());
        changeLog.setEvaluationId(baseEvaluation.getId());
        changeLog.setEditorUserName(user.getUsername());
        changeLog.setEditorName(user.getName());
        changeLog.setSociedad(baseEvaluation.getSociedad());
        changeLog.setAreaNomina(baseEvaluation.getAreaNomina());
        changeLog.setUpdatedOn(updateDate);

        return changeLog;
    }
}
