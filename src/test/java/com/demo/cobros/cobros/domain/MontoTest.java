package com.demo.cobros.cobros.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.demo.cobros.cobros.domain.exception.MontoInvalidoException;
import com.demo.cobros.cobros.domain.model.Monto;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MontoTest {

    @Test
    void montoCeroEsInvalido() {
        assertThrows(MontoInvalidoException.class, () -> new Monto(BigDecimal.ZERO, "GTQ"));
    }

    @Test
    void montoNegativoEsInvalido() {
        assertThrows(MontoInvalidoException.class, () -> new Monto(new BigDecimal("-1"), "GTQ"));
    }

    @Test
    void monedaSeNormalizaAMayusculas() {
        assertEquals("GTQ", new Monto(BigDecimal.ONE, "gtq").moneda());
    }

    @Test
    void monedaConLongitudInvalidaEsRechazada() {
        assertThrows(MontoInvalidoException.class, () -> new Monto(BigDecimal.ONE, "GUATEMALA"));
    }
}
