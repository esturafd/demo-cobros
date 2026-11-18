package com.demo.cobros.cobros.domain.model;

/**
 * Estados posibles de un cobro.
 */
public enum EstadoCobro {
    PENDIENTE,
    PROCESADO,
    FALLIDO;

    /** Un estado terminal ya no se vuelve a procesar (clave para la idempotencia). */
    public boolean esTerminal() {
        return this != PENDIENTE;
    }
}
