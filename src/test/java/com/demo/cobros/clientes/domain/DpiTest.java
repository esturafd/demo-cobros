package com.demo.cobros.clientes.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.demo.cobros.clientes.domain.exception.DpiInvalidoException;
import com.demo.cobros.clientes.domain.model.Dpi;
import org.junit.jupiter.api.Test;

class DpiTest {

    @Test
    void dpiValidoConTreceDigitos() {
        assertEquals("1234567890123", new Dpi("1234567890123").valor());
    }

    @Test
    void dpiConEspaciosSeNormaliza() {
        assertEquals("1234567890123", new Dpi("  1234567890123 ").valor());
    }

    @Test
    void dpiConLongitudInvalidaEsRechazado() {
        assertThrows(DpiInvalidoException.class, () -> new Dpi("123"));
    }

    @Test
    void dpiConLetrasEsRechazado() {
        assertThrows(DpiInvalidoException.class, () -> new Dpi("123456789012A"));
    }
}
