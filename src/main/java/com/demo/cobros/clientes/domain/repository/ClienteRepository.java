package com.demo.cobros.clientes.domain.repository;

import com.demo.cobros.clientes.domain.model.Cliente;
import java.util.Optional;

/**
 * Puerto (interfaz) de persistencia de clientes. La implementación concreta vive
 * en {@code clientes.infrastructure.persistence}. El dominio solo conoce esta
 * abstracción.
 */
public interface ClienteRepository {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    boolean existePorId(Long id);

    boolean existePorDpi(String dpi);
}
