package com.web.back.mappers;

import com.web.back.model.entities.ChangeLog;
import com.web.back.model.xls.ChangeLogXlsx;

public final class ChangeLogXlsxMapper {

    private ChangeLogXlsxMapper(){}

    public static ChangeLogXlsx mapFrom(ChangeLog log){
        return new ChangeLogXlsx(log.getNumEmpleado(),
                log.getUpdatedOn(),
                log.getField(),
                log.getOriginal(),
                log.getUpdated(),
                log.getUserId());
    }
}
