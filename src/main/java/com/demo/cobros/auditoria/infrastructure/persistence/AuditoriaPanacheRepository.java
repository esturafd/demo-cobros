package com.demo.cobros.auditoria.infrastructure.persistence;

import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.auditoria.domain.repository.AuditoriaRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<Auditoria> listar(
            EventoAuditoria evento, Instant desde, Instant hasta, int limite) {
        StringBuilder consulta = new StringBuilder("id > 0");
        Map<String, Object> parametros = new HashMap<>();

        if (evento != null) {
            consulta.append(" and evento = :evento");
            parametros.put("evento", evento.name());
        }
        if (desde != null) {
            consulta.append(" and fecha >= :desde");
            parametros.put("desde", desde);
        }
        if (hasta != null) {
            consulta.append(" and fecha <= :hasta");
            parametros.put("hasta", hasta);
        }
        consulta.append(" order by fecha desc");

        return find(consulta.toString(), parametros)
                .range(0, Math.max(0, limite - 1))
                .list()
                .stream()
                .map(mapper::aDominio)
                .toList();
    }
}
