package com.demo.cobros.shared.error;

/**
 * Se lanza cuando un parámetro de entrada (típicamente de query string) no tiene
 * un formato válido. Se mapea a HTTP 400.
 */
public class ParametroInvalidoException extends RuntimeException {

    private final String parametro;
    private final String detalle;

    public ParametroInvalidoException(String parametro, String valor, String detalle) {
        super("El parámetro '%s' con valor '%s' es inválido: %s".formatted(parametro, valor, detalle));
        this.parametro = parametro;
        this.detalle = detalle;
    }

    public String parametro() {
        return parametro;
    }

    public String detalle() {
        return detalle;
    }
}
