package com.web.back.mappers;

import com.web.back.model.dto.ChangeLogDto;
import com.web.back.model.entities.ChangeLog;

public final class ChangeLogDtoMapper {
    private ChangeLogDtoMapper() {}

    public static ChangeLogDto mapFrom(ChangeLog changeLog) {
        return new ChangeLogDto(
                changeLog.getId(),
                changeLog.getNumEmpleado(),
                changeLog.getEmpleadoName(),
                changeLog.getUpdatedOn(),
                changeLog.getField(),
                changeLog.getOriginal(),
                changeLog.getUpdated(),
                changeLog.getEditorUserName(),
                changeLog.getEditorName()
        );
    }
}
