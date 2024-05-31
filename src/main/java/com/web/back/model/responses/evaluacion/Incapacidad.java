package com.web.back.model.responses.evaluacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Incapacidad {
    private String beginDate;
    private String endDate;
    private String numEmpleado;
    private String referencia;
    private String consecutivo1;
    private String consecutivo2;
}
