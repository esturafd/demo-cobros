package com.demo.cobros.clientes.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.demo.cobros.clientes.application.dto.ClienteResponse;
import com.demo.cobros.clientes.application.dto.CrearClienteCommand;
import com.demo.cobros.clientes.domain.exception.DpiDuplicadoException;
import com.demo.cobros.clientes.domain.exception.DpiInvalidoException;
import com.demo.cobros.clientes.domain.model.Cliente;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CrearClienteUseCaseTest {

    private ClienteRepository clienteRepository;
    private RegistrarEventoUseCase registrarEvento;
    private CrearClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        registrarEvento = mock(RegistrarEventoUseCase.class);
        useCase = new CrearClienteUseCase(clienteRepository, registrarEvento);
    }

    @Test
    void creaClienteYRegistraAuditoria() {
        when(clienteRepository.existePorDpi("1234567890123")).thenReturn(false);
        when(clienteRepository.guardar(any(Cliente.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0, Cliente.class).conId(7L));

        ClienteResponse respuesta =
                useCase.ejecutar(
                        new CrearClienteCommand("Ana Pérez", "1234567890123", "ana@example.com", null),
                        "tester");

        assertEquals(7L, respuesta.id());
        assertEquals("1234567890123", respuesta.dpi());
        assertEquals("ana@example.com", respuesta.email());
        verify(registrarEvento)
                .ejecutar(eq(EventoAuditoria.CLIENTE_CREADO), anyString(), eq("tester"));
    }

    @Test
    void fallaSiElDpiYaExiste() {
        when(clienteRepository.existePorDpi("1234567890123")).thenReturn(true);

        assertThrows(
                DpiDuplicadoException.class,
                () ->
                        useCase.ejecutar(
                                new CrearClienteCommand("Ana", "1234567890123", null, null), "tester"));

        verify(clienteRepository, never()).guardar(any(Cliente.class));
        verifyNoInteractions(registrarEvento);
    }

    @Test
    void fallaSiElDpiEsInvalido() {
        assertThrows(
                DpiInvalidoException.class,
                () -> useCase.ejecutar(new CrearClienteCommand("Ana", "123", null, null), "tester"));

        verifyNoInteractions(clienteRepository);
    }
}
