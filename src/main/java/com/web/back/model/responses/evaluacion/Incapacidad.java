package com.web.back.model.responses.evaluacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Incapacidad {
    private Integer incapacidadId;
    private Date beginDate;
    private Date endDate;
    private String numEmpleado;
    private String referencia;
    private String consecutivo1;
    private String consecutivo2;
}
