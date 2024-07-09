package com.web.back.model.requests;

public record RegistroTiemposRequest(String mandt, Long empleado, String fecha, int turno, String horaEntrada, String horaSalidaPausa, String horaRegresoPausa, String horaSalida, String horario) {
}


