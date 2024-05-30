package com.web.back.mappers;

import com.web.back.model.dto.ChangeLogDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.entities.Evaluation;
import com.web.back.utils.DateUtil;

import java.util.List;
import java.util.Objects;

public final class ChangeLogDtoMapper {
    private ChangeLogDtoMapper() {}

    public static ChangeLogDto mapFrom(ChangeLog changeLog, List<Evaluation> evaluationList) {
        var evaluation = evaluationList.stream().filter(f -> Objects.equals(f.getId(), changeLog.getEvaluationId())).findFirst();

        return new ChangeLogDto(
                changeLog.getId(),
                changeLog.getNumEmpleado(),
                changeLog.getEmpleadoName(),
                changeLog.getUpdatedOn(),
                evaluation.map(value -> DateUtil.toStringYYYYMMDD(value.getFecha())).orElse(""),
                changeLog.getField(),
                changeLog.getOriginal(),
                changeLog.getUpdated(),
                changeLog.getEditorUserName(),
                changeLog.getEditorName()
        );
    }
}
