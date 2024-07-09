package com.web.back.model.dto;

public record RegistroTiemposDto(String mandt, Long numEmpleado, String fecha, int turno, String horaEntrada, String horaSalidaPausa, String horaRegresoPausa, String horaSalida, String horario) {
}
