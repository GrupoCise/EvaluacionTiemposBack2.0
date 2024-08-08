package com.web.back.mappers;

import com.web.back.model.entities.CatIncidence;
import com.web.back.model.responses.evaluacion.Incidencia;

public final class IncidenciaToCatIncidenceMapper {
    private IncidenciaToCatIncidenceMapper() {}

    public static CatIncidence map(Incidencia incidencia) {
        if (incidencia == null) {
            return null;
        }

        CatIncidence catIncidence = new CatIncidence();
        catIncidence.setMandt(incidencia.getMandt());
        catIncidence.setIdRegla(incidencia.getIdRegla());
        catIncidence.setDescripcion(incidencia.getDescripcion());
        catIncidence.setIdRetorno(incidencia.getIdRetorno());

        return catIncidence;
    }
}
