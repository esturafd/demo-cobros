package com.demo.cobros.auditoria.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.demo.cobros.auditoria.application.dto.AuditoriaResponse;
import com.demo.cobros.auditoria.domain.model.Auditoria;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.auditoria.domain.repository.AuditoriaRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsultarAuditoriaUseCaseTest {

    private AuditoriaRepository auditoriaRepository;
    private ConsultarAuditoriaUseCase useCase;

    @BeforeEach
    void setUp() {
        auditoriaRepository = mock(AuditoriaRepository.class);
        useCase = new ConsultarAuditoriaUseCase(auditoriaRepository);
    }

    @Test
    void mapeaLosEventosAlDtoDeRespuesta() {
        Auditoria evento =
                Auditoria.reconstituir(
                        1L,
                        EventoAuditoria.COBRO_PROCESADO,
                        "{\"cobroId\":1}",
                        Instant.parse("2026-01-31T10:00:00Z"),
                        "tester");
        when(auditoriaRepository.listar(eq(EventoAuditoria.COBRO_PROCESADO), any(), any(), eq(50)))
                .thenReturn(List.of(evento));

        List<AuditoriaResponse> respuesta =
                useCase.ejecutar(EventoAuditoria.COBRO_PROCESADO, null, null, 50);

        assertEquals(1, respuesta.size());
        assertEquals("COBRO_PROCESADO", respuesta.get(0).evento());
        assertEquals("tester", respuesta.get(0).usuarioSistema());
        assertEquals("{\"cobroId\":1}", respuesta.get(0).resumenPayload());
    }

    @Test
    void devuelveListaVaciaSiNoHayEventos() {
        when(auditoriaRepository.listar(any(), any(), any(), eq(50))).thenReturn(List.of());

        assertEquals(0, useCase.ejecutar(null, null, null, 50).size());
    }
}
