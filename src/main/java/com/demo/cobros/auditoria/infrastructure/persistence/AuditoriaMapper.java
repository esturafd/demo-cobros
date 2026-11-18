package com.demo.cobros.auditoria.infrastructure.persistence;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Traduce entre el modelo de dominio y la entidad JPA de auditoría.
 */
@ApplicationScoped
public class AuditoriaMapper {

    public Auditoria aDominio(AuditoriaJpaEntity entidad) {
        return Auditoria.reconstituir(
                entidad.id,
                EventoAuditoria.valueOf(entidad.evento),
                entidad.resumenPayload,
                entidad.fecha,
                entidad.usuarioSistema);
    }

    public AuditoriaJpaEntity aJpa(Auditoria auditoria) {
        AuditoriaJpaEntity entidad = new AuditoriaJpaEntity();
        entidad.id = auditoria.id();
        entidad.evento = auditoria.evento().name();
        entidad.resumenPayload = auditoria.resumenPayload();
        entidad.fecha = auditoria.fecha();
        entidad.usuarioSistema = auditoria.usuarioSistema();
        return entidad;
    }
}
