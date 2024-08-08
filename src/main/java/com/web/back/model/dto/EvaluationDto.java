package com.web.back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDto {
    private Integer id;
    private LocalDate fecha;
    private Time horaEntrada;
    private Time horaPausa;
    private Time horaRegresoPausa;
    private Time horaSalida;
    private String resultadoEntrada;
    private String resultadoPausa;
    private String resultadoRegresoPausa;
    private String resultadoSalida;
    private String resultadoGeneral;
    private String statusRegistro;
    private String numEmpleado;
    private String empleadoName;
    private String horario;
    private String comentario;
    private String enlace;
    private Short horasExtra;
    private Short horasTomadas;
    private String areaNomina;
    private String sociedad;
    private String tipoHrsExtra;
    private Integer tipoIncidencia;
    private String referencia;
    private String consecutivo1;
    private String consecutivo2;
    private Integer approbationLevel;
    private Integer turn;
    private String payroll;
}
