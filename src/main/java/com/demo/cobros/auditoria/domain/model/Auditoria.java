package com.demo.cobros.auditoria.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio {@code Auditoria}. Inmutable y libre de frameworks.
 */
public class Auditoria {

    private final Long id;
    private final EventoAuditoria evento;
    private final String resumenPayload;
    private final Instant fecha;
    private final String usuarioSistema;

    private Auditoria(
            Long id,
            EventoAuditoria evento,
            String resumenPayload,
            Instant fecha,
            String usuarioSistema) {
        this.id = id;
        this.evento = Objects.requireNonNull(evento, "El evento es obligatorio");
        this.resumenPayload = resumenPayload;
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria");
        this.usuarioSistema = usuarioSistema;
    }

    public static Auditoria nueva(
            EventoAuditoria evento, String resumenPayload, Instant fecha, String usuarioSistema) {
        return new Auditoria(null, evento, resumenPayload, fecha, usuarioSistema);
    }

    public static Auditoria reconstituir(
            Long id,
            EventoAuditoria evento,
            String resumenPayload,
            Instant fecha,
            String usuarioSistema) {
        return new Auditoria(id, evento, resumenPayload, fecha, usuarioSistema);
    }

    public Long id() {
        return id;
    }

    public EventoAuditoria evento() {
        return evento;
    }

    public String resumenPayload() {
        return resumenPayload;
    }

    public Instant fecha() {
        return fecha;
    }

    public String usuarioSistema() {
        return usuarioSistema;
    }
}
