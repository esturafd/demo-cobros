package com.demo.cobros.cobros.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.demo.cobros.auditoria.application.RegistrarEventoUseCase;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.clientes.domain.exception.ClienteNoEncontradoException;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.RegistrarCobroCommand;
import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrarCobroUseCaseTest {

    private CobroRepository cobroRepository;
    private ClienteRepository clienteRepository;
    private RegistrarEventoUseCase registrarEvento;
    private RegistrarCobroUseCase useCase;

    @BeforeEach
    void setUp() {
        cobroRepository = mock(CobroRepository.class);
        clienteRepository = mock(ClienteRepository.class);
        registrarEvento = mock(RegistrarEventoUseCase.class);
        useCase = new RegistrarCobroUseCase(cobroRepository, clienteRepository, registrarEvento);
    }

    @Test
    void registraCobroEnEstadoPendienteYAudita() {
        when(clienteRepository.existePorId(1L)).thenReturn(true);
        when(cobroRepository.guardar(any(Cobro.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0, Cobro.class).conId(10L));

        CobroResponse respuesta =
                useCase.ejecutar(new RegistrarCobroCommand(1L, new BigDecimal("500"), "GTQ"), "tester");

        assertEquals(10L, respuesta.id());
        assertEquals("PENDIENTE", respuesta.estado());
        assertNotNull(respuesta.referenciaExterna());
        verify(registrarEvento)
                .ejecutar(eq(EventoAuditoria.COBRO_REGISTRADO), anyString(), eq("tester"));
    }

    @Test
    void fallaSiElClienteNoExiste() {
        when(clienteRepository.existePorId(99L)).thenReturn(false);

        assertThrows(
                ClienteNoEncontradoException.class,
                () -> useCase.ejecutar(new RegistrarCobroCommand(99L, BigDecimal.TEN, "GTQ"), "tester"));

        verifyNoInteractions(cobroRepository);
        verifyNoInteractions(registrarEvento);
    }
}
