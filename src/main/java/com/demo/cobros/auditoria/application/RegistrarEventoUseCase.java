package com.demo.cobros.auditoria.application;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.auditoria.domain.repository.AuditoriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;

/**
 * Caso de uso transversal: registrar un evento de auditoría.
 * Otros features (p. ej. {@code cobros}) dependen de este caso de uso.
 */
@ApplicationScoped
public class RegistrarEventoUseCase {

    private final AuditoriaRepository auditoriaRepository;

    @Inject
    public RegistrarEventoUseCase(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void ejecutar(EventoAuditoria evento, String resumenPayload, String usuarioSistema) {
        Auditoria auditoria =
                Auditoria.nueva(evento, resumenPayload, Instant.now(), usuarioSistema);
        auditoriaRepository.guardar(auditoria);
    }
}
