package com.demo.cobros.auditoria.infrastructure.persistence;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.repository.AuditoriaRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Adaptador que implementa el puerto {@link AuditoriaRepository} usando Panache.
 */
@ApplicationScoped
public class AuditoriaPanacheRepository
        implements AuditoriaRepository, PanacheRepositoryBase<AuditoriaJpaEntity, Long> {

    private final AuditoriaMapper mapper;

    @Inject
    public AuditoriaPanacheRepository(AuditoriaMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Auditoria guardar(Auditoria auditoria) {
        AuditoriaJpaEntity entidad = mapper.aJpa(auditoria);
        persist(entidad);
        return mapper.aDominio(entidad);
    }
}
