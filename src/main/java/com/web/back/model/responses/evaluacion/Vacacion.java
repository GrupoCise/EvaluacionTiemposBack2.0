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
public class Vacacion {
    private Long vacacionId;
    private Date beginDate;
    private Date endDate;
    private String numEmpleado;
}
