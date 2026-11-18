package com.demo.cobros.clientes.application;

import com.demo.cobros.clientes.application.dto.ClienteResponse;
import com.demo.cobros.clientes.domain.exception.ClienteNoEncontradoException;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Caso de uso: obtener un cliente por su identificador.
 */
@ApplicationScoped
public class ObtenerClienteUseCase {

    private final ClienteRepository clienteRepository;

    @Inject
    public ObtenerClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse ejecutar(Long id) {
        return clienteRepository
                .buscarPorId(id)
                .map(ClienteResponse::desde)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }
}
