package com.demo.cobros.auditoria.application;

import com.demo.cobros.auditoria.application.dto.AuditoriaResponse;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.auditoria.domain.repository.AuditoriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

/**
 * Caso de uso: consultar los eventos de auditoría con filtros opcionales.
 */
@ApplicationScoped
public class ConsultarAuditoriaUseCase {

    private final AuditoriaRepository auditoriaRepository;

    @Inject
    public ConsultarAuditoriaUseCase(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional
    public List<AuditoriaResponse> ejecutar(
            EventoAuditoria evento, Instant desde, Instant hasta, int limite) {
        return auditoriaRepository.listar(evento, desde, hasta, limite).stream()
                .map(AuditoriaResponse::desde)
                .toList();
    }
}
