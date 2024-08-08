package com.web.back.mappers;

import com.web.back.model.dto.CatIncidenceDto;
import com.web.back.model.entities.CatIncidence;

public final class CatIncidenceDtoMapper {
    private CatIncidenceDtoMapper() {
    }

    public static CatIncidenceDto map(CatIncidence incidence) {

        return new CatIncidenceDto(incidence.getMandt(), incidence.getIdRegla(), incidence.getDescripcion(), incidence.getIdRetorno());
    }
}
