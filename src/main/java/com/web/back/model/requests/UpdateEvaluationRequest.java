package com.web.back.model.requests;

import com.web.back.model.dto.EvaluationDto;
import com.web.back.model.responses.evaluacion.Incapacidad;
import com.web.back.model.responses.evaluacion.Vacacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEvaluationRequest {
    private String userName;
    private List<EvaluationDto> updatedEvaluations;
    private List<Vacacion> vacaciones;
    private List<Incapacidad> incapacidades;
    private List<Long> approvedEvaluations;
    private String beginDate;
    private String endDate;
    private String areaNomina;
    private String sociedad;
}