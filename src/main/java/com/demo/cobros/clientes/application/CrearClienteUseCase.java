package com.demo.cobros.clientes.application;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.clientes.application.dto.ClienteResponse;
import com.demo.cobros.clientes.application.dto.CrearClienteCommand;
import com.demo.cobros.clientes.domain.exception.DpiDuplicadoException;
import com.demo.cobros.clientes.domain.model.Cliente;
import com.demo.cobros.clientes.domain.model.Dpi;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Caso de uso: crear un cliente validando que el DPI sea único.
 */
@ApplicationScoped
public class CrearClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final RegistrarEventoUseCase registrarEvento;

    @Inject
    public CrearClienteUseCase(
            ClienteRepository clienteRepository, RegistrarEventoUseCase registrarEvento) {
        this.clienteRepository = clienteRepository;
        this.registrarEvento = registrarEvento;
    }

    @Transactional
    public ClienteResponse ejecutar(CrearClienteCommand comando, String usuarioSistema) {
        Dpi dpi = new Dpi(comando.dpi());

        if (clienteRepository.existePorDpi(dpi.valor())) {
            throw new DpiDuplicadoException(dpi.valor());
        }

        Cliente cliente =
                Cliente.nuevo(comando.nombre(), dpi.valor(), comando.email(), comando.telefono());
        Cliente guardado = clienteRepository.guardar(cliente);

        registrarEvento.ejecutar(EventoAuditoria.CLIENTE_CREADO, resumen(guardado), usuarioSistema);

        return ClienteResponse.desde(guardado);
    }

    private static String resumen(Cliente cliente) {
        return "{\"clienteId\":%d,\"nombre\":\"%s\",\"dpi\":\"%s\"}"
                .formatted(cliente.id(), cliente.nombre(), cliente.dpi().valor());
    }
}
