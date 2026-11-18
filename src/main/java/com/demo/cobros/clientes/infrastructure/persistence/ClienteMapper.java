package com.demo.cobros.clientes.infrastructure.persistence;

import com.demo.cobros.clientes.domain.model.Cliente;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Traduce entre el modelo de dominio y la entidad JPA del cliente.
 */
@ApplicationScoped
public class ClienteMapper {

    public Cliente aDominio(ClienteJpaEntity entidad) {
        return Cliente.reconstituir(
                entidad.id, entidad.nombre, entidad.dpi, entidad.email, entidad.telefono);
    }

    public ClienteJpaEntity aJpa(Cliente cliente) {
        ClienteJpaEntity entidad = new ClienteJpaEntity();
        entidad.id = cliente.id();
        entidad.nombre = cliente.nombre();
        entidad.dpi = cliente.dpi().valor();
        entidad.email = cliente.email();
        entidad.telefono = cliente.telefono();
        return entidad;
    }
}
