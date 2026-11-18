package com.demo.cobros.clientes.domain.exception;

/**
 * Se lanza cuando el DPI no cumple el formato esperado (13 dígitos).
 * El mapeo a HTTP 400 se realiza en la capa compartida de errores.
 */
public class DpiInvalidoException extends RuntimeException {

    public DpiInvalidoException(String dpi) {
        super("El DPI '%s' no tiene un formato válido (se esperan 13 dígitos)".formatted(dpi));
    }
}
