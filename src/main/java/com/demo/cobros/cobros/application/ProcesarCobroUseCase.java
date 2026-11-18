package com.demo.cobros.cobros.application;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.domain.exception.CobroNoEncontradoException;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import com.demo.cobros.cobros.domain.service.ReglaProcesamientoCobro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;

/**
 * Caso de uso: procesar un cobro individual aplicando la regla de negocio
 * simulada. Es idempotente: si el cobro ya está en estado terminal, no se
 * vuelve a procesar ni se re-audita.
 */
@ApplicationScoped
public class ProcesarCobroUseCase {

    private final CobroRepository cobroRepository;
    private final RegistrarEventoUseCase registrarEvento;
    private final ReglaProcesamientoCobro regla = new ReglaProcesamientoCobro();

    @Inject
    public ProcesarCobroUseCase(
            CobroRepository cobroRepository, RegistrarEventoUseCase registrarEvento) {
        this.cobroRepository = cobroRepository;
        this.registrarEvento = registrarEvento;
    }

    @Transactional
    public CobroResponse ejecutar(Long cobroId, String usuarioSistema) {
        Cobro cobro =
                cobroRepository
                        .buscarPorId(cobroId)
                        .orElseThrow(() -> new CobroNoEncontradoException(cobroId));

        if (!cobro.estaPendiente()) {
            return CobroResponse.desde(cobro);
        }

        EstadoCobro estado = regla.evaluar(cobro.monto());
        Cobro actualizado = cobroRepository.guardar(cobro.procesado(estado, Instant.now()));

        registrarEvento.ejecutar(
                eventoDe(estado), resumen(actualizado), usuarioSistema);

        return CobroResponse.desde(actualizado);
    }

    private static EventoAuditoria eventoDe(EstadoCobro estado) {
        return estado == EstadoCobro.PROCESADO
                ? EventoAuditoria.COBRO_PROCESADO
                : EventoAuditoria.COBRO_FALLIDO;
    }

    private static String resumen(Cobro cobro) {
        return "{\"cobroId\":%d,\"monto\":%s,\"moneda\":\"%s\",\"estado\":\"%s\"}"
                .formatted(
                        cobro.id(),
                        cobro.monto().valor().toPlainString(),
                        cobro.monto().moneda(),
                        cobro.estado());
    }
}
