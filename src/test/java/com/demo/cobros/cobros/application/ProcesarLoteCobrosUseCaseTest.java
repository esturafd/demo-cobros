package com.demo.cobros.cobros.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.cobros.application.dto.ProcesarLoteCommand;
import com.demo.cobros.cobros.application.dto.ResumenLoteResponse;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.model.Monto;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcesarLoteCobrosUseCaseTest {

    private CobroRepository cobroRepository;
    private RegistrarEventoUseCase registrarEvento;
    private ProcesarLoteCobrosUseCase useCase;

    @BeforeEach
    void setUp() {
        cobroRepository = mock(CobroRepository.class);
        registrarEvento = mock(RegistrarEventoUseCase.class);
        useCase = new ProcesarLoteCobrosUseCase(cobroRepository, registrarEvento);
    }

    @Test
    void procesaPendientesYOmiteTerminalesYNoEncontrados() {
        Cobro pendienteOk = cobro(1L, "500", EstadoCobro.PENDIENTE);
        Cobro pendienteFalla = cobro(2L, "1500", EstadoCobro.PENDIENTE);
        Cobro yaProcesado = cobro(3L, "800", EstadoCobro.PROCESADO);
        when(cobroRepository.buscarPorIds(anyCollection()))
                .thenReturn(List.of(pendienteOk, pendienteFalla, yaProcesado));
        when(cobroRepository.guardar(any(Cobro.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        ResumenLoteResponse resumen =
                useCase.ejecutar(new ProcesarLoteCommand(List.of(1L, 2L, 3L, 99L)), "tester");

        assertEquals(4, resumen.total());
        assertEquals(1, resumen.procesados());
        assertEquals(1, resumen.fallidos());
        assertEquals(1, resumen.omitidos());
        assertEquals(1, resumen.noEncontrados());
        assertEquals(4, resumen.detalle().size());
        verify(registrarEvento, times(1))
                .ejecutar(eq(EventoAuditoria.LOTE_PROCESADO), anyString(), eq("tester"));
    }

    @Test
    void esIdempotenteAlReenviarElMismoLote() {
        Cobro pendienteOk = cobro(1L, "500", EstadoCobro.PENDIENTE);
        when(cobroRepository.buscarPorIds(anyCollection())).thenReturn(List.of(pendienteOk));
        when(cobroRepository.guardar(any(Cobro.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        ResumenLoteResponse primera =
                useCase.ejecutar(new ProcesarLoteCommand(List.of(1L)), "tester");
        assertEquals(1, primera.procesados());

        // En el segundo envío el cobro ya es terminal: se omite.
        Cobro yaProcesado = cobro(1L, "500", EstadoCobro.PROCESADO);
        when(cobroRepository.buscarPorIds(anyCollection())).thenReturn(List.of(yaProcesado));

        ResumenLoteResponse segunda =
                useCase.ejecutar(new ProcesarLoteCommand(List.of(1L)), "tester");

        assertEquals(0, segunda.procesados());
        assertEquals(1, segunda.omitidos());
    }

    private static Cobro cobro(Long id, String monto, EstadoCobro estado) {
        Instant fechaProceso = estado.esTerminal() ? Instant.now() : null;
        return Cobro.reconstituir(
                id,
                1L,
                new Monto(new BigDecimal(monto), "GTQ"),
                estado,
                Instant.now(),
                fechaProceso,
                "ref-" + id);
    }
}
