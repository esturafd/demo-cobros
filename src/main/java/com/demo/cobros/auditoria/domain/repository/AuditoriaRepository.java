package com.demo.cobros.auditoria.domain.repository;

import com.demo.cobros.auditoria.domain.model.Auditoria;

/**
 * Puerto de persistencia de la auditoría.
 */
public interface AuditoriaRepository {

    Auditoria guardar(Auditoria auditoria);
}
