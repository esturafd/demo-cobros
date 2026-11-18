package com.demo.cobros.shared.error;

import java.time.Instant;
import java.util.List;

/**
 * Cuerpo de error uniforme para todas las respuestas de error de la API.
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<DetalleError> details) {

    public record DetalleError(String field, String message) {}

    public static ErrorResponse de(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, List.of());
    }

    public static ErrorResponse conDetalles(
            int status, String error, String message, String path, List<DetalleError> details) {
        return new ErrorResponse(Instant.now(), status, error, message, path, details);
    }
}
