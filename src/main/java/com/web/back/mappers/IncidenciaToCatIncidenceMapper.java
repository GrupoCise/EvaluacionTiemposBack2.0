package com.web.back.mappers;

import com.web.back.model.entities.CatIncidence;
import com.web.back.model.responses.evaluacion.Incidencia;

public final class IncidenciaToCatIncidenceMapper {
    private IncidenciaToCatIncidenceMapper() {}

    public static CatIncidence map(Incidencia incidencia, String sociedad, String areaNomina) {
        if (incidencia == null) {
            return null;
        }

        CatIncidence catIncidence = new CatIncidence();
        catIncidence.setMandt(incidencia.getMandt());
        catIncidence.setIdRegla(incidencia.getIdRegla());
        catIncidence.setDescripcion(incidencia.getDescripcion());
        catIncidence.setIdRetorno(incidencia.getIdRetorno());
        catIncidence.setSociedad(sociedad);
        catIncidence.setAreaNomina(areaNomina);

        return catIncidence;
    }
}
