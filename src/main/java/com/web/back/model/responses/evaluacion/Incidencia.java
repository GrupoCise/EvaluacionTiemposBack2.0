package com.web.back.model.responses.evaluacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Incidencia {
    private String mandt;
    private String idRegla;
    private String descripcion;
    private String idRetorno;
}
