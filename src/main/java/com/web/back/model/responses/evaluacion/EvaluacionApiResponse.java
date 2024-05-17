package com.web.back.model.responses.evaluacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EvaluacionApiResponse {
    List<Employee> empleados;
    List<Sociedad> sociedades;
    List<Horario> horarios;
    List<AreaNomina> areasNomina;
    List<Calendario> calendario;
    List<Incidencia> catIncidencias;
}
