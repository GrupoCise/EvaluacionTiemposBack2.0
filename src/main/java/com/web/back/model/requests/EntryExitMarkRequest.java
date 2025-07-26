package com.web.back.model.requests;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record EntryExitMarkRequest(
        String empleado,
        String fecha,
        String hora,
        String tipo) {

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "EntryExitMarkRequest{" +
                "empleado='" + empleado + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
