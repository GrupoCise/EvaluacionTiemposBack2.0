package com.web.back.builders;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.entities.User;
import org.apache.commons.lang3.ObjectUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChangeLogBuilder {
    private ChangeLogBuilder() {
    }

    public static List<ChangeLog> buildFrom(Evaluation original, EvaluationDto updated, User user) {
        return buildChangeLogs(original, updated, user, true);
    }

    public static List<ChangeLog> buildFrom(Evaluation original, Evaluation updated, User user) {
        return buildChangeLogs(original, updated, user, false);
    }

    private static List<ChangeLog> buildChangeLogs(Evaluation original, Object updated, User user, boolean isDto) {
        var updateDate = Instant.now();

        return Stream.of(
                        build("Horas Tomadas", original.getHorasTomadas(), getUpdatedValue(updated, "getHorasTomadas"), updated, original, user, updateDate, isDto),
                        build("Horas Extras", original.getHorasExtra(), getUpdatedValue(updated, "getHorasExtra"), updated, original, user, updateDate, isDto),
                        build("Horario", original.getHorario(), getUpdatedValue(updated, "getHorario"), updated, original, user, updateDate, isDto),
                        build("Hora Entrada", original.getHoraEntrada(), getUpdatedValue(updated, "getHoraEntrada"), updated, original, user, updateDate, isDto),
                        build("Hora Salida", original.getHoraSalida(), getUpdatedValue(updated, "getHoraSalida"), updated, original, user, updateDate, isDto),
                        build("Resultado General", original.getResultadoGeneral(), getUpdatedValue(updated, "getResultadoGeneral"), updated, original, user, updateDate, isDto),
                        build("Comentario", original.getComentario(), getUpdatedValue(updated, "getComentario"), updated, original, user, updateDate, isDto),
                        build("Enlace", original.getEnlace(), getUpdatedValue(updated, "getEnlace"), updated, original, user, updateDate, isDto)
                ).filter(changeLog -> !Objects.equals(changeLog.getOriginal(), changeLog.getUpdated()))
                .collect(Collectors.toList());
    }

    private static Object getUpdatedValue(Object updated, String methodName) {
        try {
            return updated.getClass().getMethod(methodName).invoke(updated);
        } catch (Exception e) {
            throw new RuntimeException("Error getting updated value", e);
        }
    }

    private static ChangeLog build(String field,
                                   Object originalValue,
                                   Object updatedValue,
                                   Object updated,
                                   Evaluation baseEvaluation,
                                   User user,
                                   Instant updateDate,
                                   boolean isDto) {
        ChangeLog changeLog = new ChangeLog();

        changeLog.setField(field);
        changeLog.setOriginal(ObjectUtils.isNotEmpty(originalValue) ? originalValue.toString() : null);
        changeLog.setUpdated(ObjectUtils.isNotEmpty(updatedValue) ? updatedValue.toString() : null);
        changeLog.setNumEmpleado(baseEvaluation.getNumEmpleado());
        changeLog.setEmpleadoName(isDto ? ((EvaluationDto) updated).getEmpleadoName() : ((Evaluation) updated).getEmployeeName());
        changeLog.setEvaluationId(baseEvaluation.getId());
        changeLog.setEditorUserName(user.getUsername());
        changeLog.setEditorName(user.getName());
        changeLog.setSociedad(baseEvaluation.getSociedad());
        changeLog.setAreaNomina(baseEvaluation.getAreaNomina());
        changeLog.setUpdatedOn(updateDate);

        return changeLog;
    }
}
