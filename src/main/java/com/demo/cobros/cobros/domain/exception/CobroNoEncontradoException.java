package com.demo.cobros.cobros.domain.exception;

/**
 * Se lanza cuando no se encuentra un cobro por su identificador.
 * El mapeo a HTTP 404 se realiza en la capa compartida de errores.
 */
public class CobroNoEncontradoException extends RuntimeException {

    public CobroNoEncontradoException(Long id) {
        super("No existe el cobro con id %d".formatted(id));
    }
}
