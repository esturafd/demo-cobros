package com.demo.cobros.auditoria.domain.model;

/**
 * Eventos de negocio que se registran en la auditoría del sistema.
 */
public enum EventoAuditoria {
    CLIENTE_CREADO,
    COBRO_REGISTRADO,
    COBRO_PROCESADO,
    COBRO_FALLIDO,
    LOTE_PROCESADO
}
