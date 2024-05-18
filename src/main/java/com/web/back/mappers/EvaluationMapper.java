package com.web.back.mappers;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.entities.Evaluation;
import com.web.back.model.responses.EmployeeApiResponse;

import java.lang.reflect.Field;
import java.util.Optional;

public final class EvaluationMapper {
    private EvaluationMapper() {}

    public static Evaluation mapFrom(EmployeeApiResponse employee, String sociedad, String areaNomina) {
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
        evaluation.setResultadoGeneral("0");
        evaluation.setStatusRegistro(employee.getEstatusGen());
        evaluation.setNumEmpleado(employee.getEmployeeNumber());
        evaluation.setHorario(employee.getHorario());
        evaluation.setHorasExtra(employee.getHrsextradia());
        evaluation.setTipoHrsExtra(employee.getTipoHrsext());
        evaluation.setSociedad(sociedad);
        evaluation.setAreaNomina(areaNomina);

        try {
            Field[] fields = employee.getClass().getDeclaredFields();
            for (Field field : fields) {
                evaluation.addPropertyPayload(field.getName(), field.get(employee));
            }
        }catch (Exception ignored) {}

        return evaluation;
    }

    public static Evaluation mapFrom(EvaluationDto evaluation, Optional<Evaluation> original){
        return new Evaluation(
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
                evaluation.getIncapacidad(),
                evaluation.getAprobado(),
                evaluation.getHorasExtra(),
                evaluation.getHorasTomadas(),
                original.<java.util.Map<String, Object>>map(Evaluation::getPayload).orElse(null),
                evaluation.getAreaNomina(),
                evaluation.getSociedad(),
                evaluation.getTipoHrsExtra());
    }
}
