package com.web.back.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeApiResponse {
    private String employeeNumber;
    private Date fecha;
    private Time horaEntrada;
    private Time horaSPausa;
    private Time horaEPausa;
    private Time horaSalida;
    private String rHoraEntrada;
    private String rHoraSPausa;
    private String rHoraEPausa;
    private String rHoraSalida;
    private String estatusGen;
    private String horario;
    private Short hrsextradia;
    private String tipoHrsext;
}
