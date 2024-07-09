package com.web.back.mappers;

import com.web.back.model.dto.RegistroTiemposDto;
import com.web.back.model.requests.RegistroTiemposRequest;
import com.web.back.model.responses.RegistroTiemposResponse;

import java.util.List;

public final class RegistroTiemposDtoMapper {
    private RegistroTiemposDtoMapper() {}

    public static List<RegistroTiemposDto> mapFrom(List<RegistroTiemposResponse> responses) {
        return responses.stream()
                .map(response -> new RegistroTiemposDto(
                    response.mandt(),
                    response.empleado(),
                    response.fecha(),
                    response.turno(),
                    response.horaEntrada(),
                    response.horaSalidaPausa(),
                    response.horaRegresoPausa(),
                    response.horaSalida(),
                    response.horario())
                ).toList();
    }

    public static List<RegistroTiemposRequest> mapToRequestFrom(List<RegistroTiemposDto> responses) {
        return responses.stream()
                .map(response -> new RegistroTiemposRequest(
                        response.mandt(),
                        response.numEmpleado(),
                        response.fecha(),
                        response.turno(),
                        response.horaEntrada(),
                        response.horaSalidaPausa(),
                        response.horaRegresoPausa(),
                        response.horaSalida(),
                        response.horario())
                ).toList();
    }
}
