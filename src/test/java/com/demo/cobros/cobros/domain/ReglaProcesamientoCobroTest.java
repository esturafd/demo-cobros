package com.demo.cobros.cobros.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.model.Monto;
import com.demo.cobros.cobros.domain.service.ReglaProcesamientoCobro;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Pruebas puras de la regla de negocio simulada (sin frameworks). */
class ReglaProcesamientoCobroTest {

    private final ReglaProcesamientoCobro regla = new ReglaProcesamientoCobro();

    @Test
    void montoMenorAlUmbralSeProcesa() {
        assertEquals(EstadoCobro.PROCESADO, regla.evaluar(new Monto(new BigDecimal("500.00"), "GTQ")));
    }

    @Test
    void montoIgualAlUmbralSeProcesa() {
        assertEquals(EstadoCobro.PROCESADO, regla.evaluar(new Monto(new BigDecimal("1000"), "GTQ")));
    }

    @Test
    void montoMayorAlUmbralFalla() {
        assertEquals(
                EstadoCobro.FALLIDO, regla.evaluar(new Monto(new BigDecimal("1000.01"), "GTQ")));
    }
}
