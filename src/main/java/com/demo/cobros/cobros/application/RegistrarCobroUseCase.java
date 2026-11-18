package com.demo.cobros.cobros.application;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.clientes.domain.exception.ClienteNoEncontradoException;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.RegistrarCobroCommand;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.Monto;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: registrar un cobro nuevo en estado PENDIENTE.
 * Valida que el cliente exista y deja traza en auditoría.
 */
@ApplicationScoped
public class RegistrarCobroUseCase {

    private final CobroRepository cobroRepository;
    private final ClienteRepository clienteRepository;
    private final RegistrarEventoUseCase registrarEvento;

    @Inject
    public RegistrarCobroUseCase(
            CobroRepository cobroRepository,
            ClienteRepository clienteRepository,
            RegistrarEventoUseCase registrarEvento) {
        this.cobroRepository = cobroRepository;
        this.clienteRepository = clienteRepository;
        this.registrarEvento = registrarEvento;
    }

    @Transactional
    public CobroResponse ejecutar(RegistrarCobroCommand comando, String usuarioSistema) {
        if (!clienteRepository.existePorId(comando.clienteId())) {
            throw new ClienteNoEncontradoException(comando.clienteId());
        }

        Monto monto = new Monto(comando.monto(), comando.moneda());
        Cobro cobro =
                Cobro.nuevo(comando.clienteId(), monto, UUID.randomUUID().toString(), Instant.now());
        Cobro guardado = cobroRepository.guardar(cobro);

        registrarEvento.ejecutar(
                EventoAuditoria.COBRO_REGISTRADO, resumen(guardado), usuarioSistema);

        return CobroResponse.desde(guardado);
    }

    private static String resumen(Cobro cobro) {
        return "{\"cobroId\":%d,\"clienteId\":%d,\"monto\":%s,\"moneda\":\"%s\",\"estado\":\"%s\"}"
                .formatted(
                        cobro.id(),
                        cobro.clienteId(),
                        cobro.monto().valor().toPlainString(),
                        cobro.monto().moneda(),
                        cobro.estado());
    }
}
