package com.web.back.model.dto;

import java.time.Instant;

public record ChangeLogDto(
        Integer id,
        String numEmpleado,
        String empleadoName,
        Instant updatedOn,
        String fecha,
        String field,
        String original,
        String updated,
        String userNumber,
        String userName){
}
