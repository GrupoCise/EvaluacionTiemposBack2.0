package com.web.back.model.responses;

public record RegistroTiemposResponse(String mandt, Long empleado, String fecha, int turno, String horaEntrada, String horaSalidaPausa, String horaRegresoPausa, String horaSalida, String horario) {
}
