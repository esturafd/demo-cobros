package com.demo.cobros.clientes.infrastructure.persistence;

import com.demo.cobros.clientes.domain.exception.DpiDuplicadoException;
import com.demo.cobros.clientes.domain.model.Cliente;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
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
        try {
            ClienteJpaEntity entidad = mapper.aJpa(cliente);
            if (entidad.id == null) {
                persist(entidad);
                return mapper.aDominio(entidad);
            }
            ClienteJpaEntity fusionada = getEntityManager().merge(entidad);
            return mapper.aDominio(fusionada);
        } catch (PersistenceException excepcion) {
            // Red de seguridad ante la restricción única del DPI (por si la
            // validación previa del caso de uso no atrapó el duplicado).
            if (esViolacionDeUnicidad(excepcion)) {
                throw new DpiDuplicadoException(cliente.dpi().valor());
            }
            throw excepcion;
        }
    }

    private static boolean esViolacionDeUnicidad(PersistenceException excepcion) {
        for (Throwable causa = excepcion; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.contains("UNIQUE")) {
                return true;
            }
        }
        return false;
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
