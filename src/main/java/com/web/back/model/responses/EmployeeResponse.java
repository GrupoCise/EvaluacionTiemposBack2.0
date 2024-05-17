package com.web.back.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeResponse {
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
    private String hrsextradia;
    private String tipoHrsext;

    //TODO: CHECK LATER
//    public EmployeeDto(RegistrosTrabajo registrosTrabajo){
//        this.employeeNumber = registrosTrabajo.getEmpleado().getNum_empleado();
//        this.fecha = registrosTrabajo.getFecha();
//        this.horaEntrada = registrosTrabajo.getHoraEntrada();
//        this.horaSPausa = registrosTrabajo.getHoraPausa();
//        this.horaEPausa = registrosTrabajo.getHoraRegresoPausa();
//        this.horaSalida = registrosTrabajo.getHoraSalida();
//        this.rHoraEntrada = registrosTrabajo.getResultadoEntrada();
//        this.rHoraSPausa = registrosTrabajo.getResultadoPausa();
//        this.rHoraEPausa = registrosTrabajo.getResultadoRegresoPausa();
//        this.rHoraSalida = registrosTrabajo.getResultadoSalida();
//        this.estatusGen = registrosTrabajo.getResultadoGeneral();
//        this.horario = registrosTrabajo.getHorario().getIdHorario();
//    }
}
