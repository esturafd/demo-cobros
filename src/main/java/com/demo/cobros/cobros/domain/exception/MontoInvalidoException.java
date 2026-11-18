package com.demo.cobros.cobros.domain.exception;

/**
 * Se lanza cuando el monto de un cobro es inválido (no positivo) o la moneda
 * no tiene el formato esperado. El mapeo a HTTP 400 vive en la capa de errores.
 */
public class MontoInvalidoException extends RuntimeException {

    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
