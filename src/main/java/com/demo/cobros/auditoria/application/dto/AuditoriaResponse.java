package com.demo.cobros.auditoria.application.dto;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import java.time.Instant;

/**
 * Representación de un evento de auditoría para las respuestas de la API.
 */
public record AuditoriaResponse(
        Long id, String evento, String resumenPayload, Instant fecha, String usuarioSistema) {

    public static AuditoriaResponse desde(Auditoria auditoria) {
        return new AuditoriaResponse(
                auditoria.id(),
                auditoria.evento().name(),
                auditoria.resumenPayload(),
                auditoria.fecha(),
                auditoria.usuarioSistema());
    }
}
