package com.web.back.mappers;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.Employee;

import java.lang.reflect.Field;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Map;

public final class EvaluationMapper {
    private EvaluationMapper() {}

    public static Evaluation mapFrom(EmployeeApiResponse employee, Employee employeeData, String sociedad, String areaNomina) {
        var evaluation = new Evaluation();

        evaluation.setFecha(employee.getFecha());
        evaluation.setHoraEntrada(employee.getHoraEntrada());
        evaluation.setHoraPausa(employee.getHoraSPausa());
        evaluation.setHoraRegresoPausa(employee.getHoraEPausa());
        evaluation.setHoraSalida(employee.getHoraSalida());
        evaluation.setResultadoEntrada(employee.getRHoraEntrada());
        evaluation.setResultadoPausa(employee.getRHoraSPausa());
        evaluation.setResultadoRegresoPausa(employee.getRHoraEPausa());
        evaluation.setResultadoSalida(employee.getRHoraSalida());
        evaluation.setResultadoGeneral(employee.getEstatusGen());
        evaluation.setStatusRegistro(null);
        evaluation.setNumEmpleado(employee.getEmpleado());
        evaluation.setHorario(employee.getHorario());
        evaluation.setHorasExtra(employee.getHrsextradia());
        evaluation.setTipoHrsExtra(employee.getTipoHrsext());
        evaluation.setSociedad(sociedad);
        evaluation.setAreaNomina(areaNomina);
        evaluation.setTurn(employee.getTurno());

        if(employeeData != null) {
            evaluation.setEmployeeName(employeeData.getNombre());
            evaluation.setPayroll(employeeData.getVdsk1());
        }

        try {
            Field[] fields = employee.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                evaluation.addPropertyPayload(field.getName(), field.get(employee));
            }
        }catch (Exception ignored) {}

        return evaluation;
    }

    public static Evaluation mapFrom(EvaluationDto evaluation, Evaluation original){
        return toEvaluation(
                evaluation.getFecha(),
                evaluation.getId(),
                evaluation.getHoraEntrada(),
                evaluation.getHoraPausa(),
                evaluation.getHoraRegresoPausa(),
                evaluation.getHoraSalida(),
                evaluation.getResultadoEntrada(),
                evaluation.getResultadoPausa(),
                evaluation.getResultadoRegresoPausa(),
                evaluation.getResultadoSalida(),
                evaluation.getResultadoGeneral(),
                evaluation.getStatusRegistro(),
                evaluation.getNumEmpleado(),
                evaluation.getHorario(),
                evaluation.getComentario(),
                evaluation.getEnlace(),
                evaluation.getHorasExtra(),
                evaluation.getHorasTomadas(),
                original.getPayload(),
                evaluation.getAreaNomina(),
                evaluation.getSociedad(),
                evaluation.getTipoHrsExtra(),
                evaluation.getReferencia(),
                evaluation.getConsecutivo1(),
                evaluation.getConsecutivo2(),
                evaluation.getApprobationLevel(),
                evaluation.getTurn(),
                evaluation.getEmpleadoName(),
                evaluation.getPayroll());
    }

    public static Evaluation mapFromTimeSheetUpdate(Evaluation original, String resultadoGeneral, String horario) {
        return toEvaluation(
                original.getFecha(),
                original.getId(),
                original.getHoraEntrada(),
                original.getHoraPausa(),
                original.getHoraRegresoPausa(),
                original.getHoraSalida(),
                original.getResultadoEntrada(),
                original.getResultadoPausa(),
                original.getResultadoRegresoPausa(),
                original.getResultadoSalida(),
                resultadoGeneral,
                original.getStatusRegistro(),
                original.getNumEmpleado(),
                horario,
                original.getComentario(),
                original.getEnlace(),
                original.getHorasExtra(),
                original.getHorasTomadas(),
                original.getPayload(),
                original.getAreaNomina(),
                original.getSociedad(),
                original.getTipoHrsExtra(),
                original.getReferencia(),
                original.getConsecutivo1(),
                original.getConsecutivo2(),
                original.getApprobationLevel(),
                original.getTurn(),
                original.getEmployeeName(),
                original.getPayroll());
    }

    public static Evaluation toEvaluation(LocalDate fecha, Integer id, Time horaEntrada, Time horaPausa, Time horaRegresoPausa, Time horaSalida, String resultadoEntrada, String resultadoPausa, String resultadoRegresoPausa, String resultadoSalida, String resultadoGeneral, String statusRegistro, String numEmpleado, String horario, String comentario, String enlace, Short horasExtra, Short horasTomadas, Map<String, Object> payload, String areaNomina, String sociedad, String tipoHrsExtra, String referencia, String consecutivo1, String consecutivo2, Integer approbationLevel, Integer turn, String employeeName, String payroll) {
        var evaluation = new Evaluation();

        evaluation.setFecha(fecha);
        evaluation.setId(id);
        evaluation.setHoraEntrada(horaEntrada);
        evaluation.setHoraPausa(horaPausa);
        evaluation.setHoraRegresoPausa(horaRegresoPausa);
        evaluation.setHoraSalida(horaSalida);
        evaluation.setResultadoEntrada(resultadoEntrada);
        evaluation.setResultadoPausa(resultadoPausa);
        evaluation.setResultadoRegresoPausa(resultadoRegresoPausa);
        evaluation.setResultadoSalida(resultadoSalida);
        evaluation.setResultadoGeneral(resultadoGeneral);
        evaluation.setStatusRegistro(statusRegistro);
        evaluation.setNumEmpleado(numEmpleado);
        evaluation.setHorario(horario);
        evaluation.setComentario(comentario);
        evaluation.setEnlace(enlace);
        evaluation.setHorasExtra(horasExtra);
        evaluation.setHorasTomadas(horasTomadas);
        evaluation.setPayload(payload);
        evaluation.setAreaNomina(areaNomina);
        evaluation.setSociedad(sociedad);
        evaluation.setTipoHrsExtra(tipoHrsExtra);
        evaluation.setReferencia(referencia);
        evaluation.setConsecutivo1(consecutivo1);
        evaluation.setConsecutivo2(consecutivo2);
        evaluation.setApprobationLevel(approbationLevel);
        evaluation.setTurn(turn);
        evaluation.setEmployeeName(employeeName);
        evaluation.setPayroll(payroll);

        return evaluation;
    }
}
