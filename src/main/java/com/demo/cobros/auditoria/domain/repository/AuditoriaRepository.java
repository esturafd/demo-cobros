package com.demo.cobros.auditoria.domain.repository;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import java.time.Instant;
import java.util.List;

/**
 * Puerto de persistencia de la auditoría.
 */
public interface AuditoriaRepository {

    Auditoria guardar(Auditoria auditoria);

    /**
     * Lista los eventos de auditoría aplicando filtros opcionales, ordenados por
     * fecha descendente y limitados a {@code limite} resultados.
     *
     * @param evento filtro por tipo de evento (opcional)
     * @param desde  fecha mínima inclusive (opcional)
     * @param hasta  fecha máxima inclusive (opcional)
     * @param limite número máximo de resultados
     */
    List<Auditoria> listar(EventoAuditoria evento, Instant desde, Instant hasta, int limite);
}
