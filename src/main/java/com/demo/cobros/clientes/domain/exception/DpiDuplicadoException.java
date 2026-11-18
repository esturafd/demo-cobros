package com.demo.cobros.clientes.domain.exception;

/**
 * Se lanza cuando se intenta registrar un cliente con un DPI ya existente.
 * El mapeo a HTTP 409 se realiza en la capa compartida de errores.
 */
public class DpiDuplicadoException extends RuntimeException {

    public DpiDuplicadoException(String dpi) {
        super("Ya existe un cliente con el DPI '%s'".formatted(dpi));
    }
}
