package com.demo.cobros.cobros.application;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.cobros.application.dto.ProcesarLoteCommand;
import com.demo.cobros.cobros.application.dto.ResultadoCobroLote;
import com.demo.cobros.cobros.application.dto.ResumenLoteResponse;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import com.demo.cobros.cobros.domain.service.ReglaProcesamientoCobro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Caso de uso: procesar un lote de cobros.
 * <p>El proceso es <strong>idempotente</strong>: los cobros que ya están en estado
 * terminal se omiten (no se reprocesan ni se re-auditan), de modo que reenviar el
 * mismo lote produce el mismo resultado.
 */
@ApplicationScoped
public class ProcesarLoteCobrosUseCase {

    private final CobroRepository cobroRepository;
    private final RegistrarEventoUseCase registrarEvento;
    private final ReglaProcesamientoCobro regla = new ReglaProcesamientoCobro();

    @Inject
    public ProcesarLoteCobrosUseCase(
            CobroRepository cobroRepository, RegistrarEventoUseCase registrarEvento) {
        this.cobroRepository = cobroRepository;
        this.registrarEvento = registrarEvento;
    }

    @Transactional
    public ResumenLoteResponse ejecutar(ProcesarLoteCommand comando, String usuarioSistema) {
        List<Long> ids = comando.cobroIds().stream().distinct().toList();
        Map<Long, Cobro> existentes =
                cobroRepository.buscarPorIds(ids).stream()
                        .collect(Collectors.toMap(Cobro::id, Function.identity()));

        int procesados = 0;
        int fallidos = 0;
        int omitidos = 0;
        int noEncontrados = 0;
        List<ResultadoCobroLote> detalle = new ArrayList<>();

        for (Long id : ids) {
            Cobro cobro = existentes.get(id);

            if (cobro == null) {
                noEncontrados++;
                detalle.add(ResultadoCobroLote.noEncontrado(id));
                continue;
            }

            if (!cobro.estaPendiente()) {
                omitidos++;
                detalle.add(ResultadoCobroLote.omitido(id, cobro.estado().name()));
                continue;
            }

            EstadoCobro estado = regla.evaluar(cobro.monto());
            Cobro actualizado = cobroRepository.guardar(cobro.procesado(estado, Instant.now()));

            if (estado == EstadoCobro.PROCESADO) {
                procesados++;
            } else {
                fallidos++;
            }

            registrarEvento.ejecutar(eventoDe(estado), resumen(actualizado), usuarioSistema);
            detalle.add(ResultadoCobroLote.procesado(id, estado.name()));
        }

        ResumenLoteResponse resumen =
                new ResumenLoteResponse(
                        ids.size(), procesados, fallidos, omitidos, noEncontrados, detalle);

        registrarEvento.ejecutar(
                EventoAuditoria.LOTE_PROCESADO, resumenJson(resumen), usuarioSistema);

        return resumen;
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

    private static String resumenJson(ResumenLoteResponse resumen) {
        return "{\"total\":%d,\"procesados\":%d,\"fallidos\":%d,\"omitidos\":%d,\"noEncontrados\":%d}"
                .formatted(
                        resumen.total(),
                        resumen.procesados(),
                        resumen.fallidos(),
                        resumen.omitidos(),
                        resumen.noEncontrados());
    }
}
