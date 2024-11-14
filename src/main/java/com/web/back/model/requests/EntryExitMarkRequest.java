package com.web.back.model.requests;

public record EntryExitMarkRequest(
        String empleado,
        String fecha,
        String hora,
        String tipo) {
}
