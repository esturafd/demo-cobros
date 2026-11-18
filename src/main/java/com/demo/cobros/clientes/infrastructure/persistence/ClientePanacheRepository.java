package com.demo.cobros.clientes.infrastructure.persistence;

import com.demo.cobros.clientes.domain.model.Cliente;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto {@link ClienteRepository} usando Panache
 * en modo <em>repository</em> (no active record).
 */
@ApplicationScoped
public class ClientePanacheRepository
        implements ClienteRepository, PanacheRepositoryBase<ClienteJpaEntity, Long> {

    private final ClienteMapper mapper;

    @Inject
    public ClientePanacheRepository(ClienteMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteJpaEntity entidad = mapper.aJpa(cliente);
        if (entidad.id == null) {
            persist(entidad);
            return mapper.aDominio(entidad);
        }
        ClienteJpaEntity fusionada = getEntityManager().merge(entidad);
        return mapper.aDominio(fusionada);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return findByIdOptional(id).map(mapper::aDominio);
    }

    @Override
    public boolean existePorId(Long id) {
        return count("id = ?1", id) > 0;
    }

    @Override
    public boolean existePorDpi(String dpi) {
        return count("dpi = ?1", dpi) > 0;
    }
}
