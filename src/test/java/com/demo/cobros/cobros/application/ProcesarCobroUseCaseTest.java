package com.demo.cobros.cobros.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.domain.exception.CobroNoEncontradoException;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.model.Monto;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcesarCobroUseCaseTest {

    private CobroRepository cobroRepository;
    private RegistrarEventoUseCase registrarEvento;
    private ProcesarCobroUseCase useCase;

    @BeforeEach
    void setUp() {
        cobroRepository = mock(CobroRepository.class);
        registrarEvento = mock(RegistrarEventoUseCase.class);
        useCase = new ProcesarCobroUseCase(cobroRepository, registrarEvento);
    }

    @Test
    void montoMenorIgualAlUmbralQuedaProcesado() {
        when(cobroRepository.buscarPorId(1L)).thenReturn(Optional.of(pendiente(1L, "1000")));
        when(cobroRepository.guardar(any(Cobro.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        CobroResponse respuesta = useCase.ejecutar(1L, "tester");

        assertEquals("PROCESADO", respuesta.estado());
        assertNotNull(respuesta.fechaProceso());
        verify(registrarEvento)
                .ejecutar(eq(EventoAuditoria.COBRO_PROCESADO), anyString(), eq("tester"));
    }

    @Test
    void montoMayorAlUmbralQuedaFallido() {
        when(cobroRepository.buscarPorId(1L)).thenReturn(Optional.of(pendiente(1L, "1000.01")));
        when(cobroRepository.guardar(any(Cobro.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        CobroResponse respuesta = useCase.ejecutar(1L, "tester");

        assertEquals("FALLIDO", respuesta.estado());
        verify(registrarEvento)
                .ejecutar(eq(EventoAuditoria.COBRO_FALLIDO), anyString(), eq("tester"));
    }

    @Test
    void noReprocesaUnCobroYaTerminal() {
        Cobro terminal =
                Cobro.reconstituir(
                        1L,
                        1L,
                        new Monto(new BigDecimal("500"), "GTQ"),
                        EstadoCobro.PROCESADO,
                        Instant.now(),
                        Instant.now(),
                        "ref-1");
        when(cobroRepository.buscarPorId(1L)).thenReturn(Optional.of(terminal));

        CobroResponse respuesta = useCase.ejecutar(1L, "tester");

        assertEquals("PROCESADO", respuesta.estado());
        verify(cobroRepository, never()).guardar(any(Cobro.class));
        verifyNoInteractions(registrarEvento);
    }

    @Test
    void fallaSiElCobroNoExiste() {
        when(cobroRepository.buscarPorId(404L)).thenReturn(Optional.empty());

        assertThrows(CobroNoEncontradoException.class, () -> useCase.ejecutar(404L, "tester"));
    }

    private static Cobro pendiente(Long id, String monto) {
        return Cobro.reconstituir(
                id,
                1L,
                new Monto(new BigDecimal(monto), "GTQ"),
                EstadoCobro.PENDIENTE,
                Instant.now(),
                null,
                "ref-" + id);
    }
}
