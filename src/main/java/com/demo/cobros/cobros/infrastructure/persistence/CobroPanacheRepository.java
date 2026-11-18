package com.demo.cobros.cobros.infrastructure.persistence;

import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto {@link CobroRepository} usando Panache.
 */
@ApplicationScoped
public class CobroPanacheRepository
        implements CobroRepository, PanacheRepositoryBase<CobroJpaEntity, Long> {

    private final CobroMapper mapper;

    @Inject
    public CobroPanacheRepository(CobroMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Cobro guardar(Cobro cobro) {
        CobroJpaEntity entidad = mapper.aJpa(cobro);
        if (entidad.id == null) {
            persist(entidad);
            return mapper.aDominio(entidad);
        }
        return mapper.aDominio(getEntityManager().merge(entidad));
    }

    @Override
    public Optional<Cobro> buscarPorId(Long id) {
        return findByIdOptional(id).map(mapper::aDominio);
    }

    @Override
    public List<Cobro> buscarPorIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return find("id in ?1", ids).list().stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Cobro> buscarPorCliente(
            Long clienteId, EstadoCobro estado, Instant desde, Instant hasta) {
        StringBuilder consulta = new StringBuilder("clienteId = :clienteId");
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("clienteId", clienteId);

        if (estado != null) {
            consulta.append(" and estado = :estado");
            parametros.put("estado", estado.name());
        }
        if (desde != null) {
            consulta.append(" and fechaCreacion >= :desde");
            parametros.put("desde", desde);
        }
        if (hasta != null) {
            consulta.append(" and fechaCreacion <= :hasta");
            parametros.put("hasta", hasta);
        }
        consulta.append(" order by fechaCreacion desc");

        return find(consulta.toString(), parametros).list().stream()
                .map(mapper::aDominio)
                .toList();
    }
}
