package com.demo.cobros.cobros.domain.service;

import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.model.Monto;
import java.math.BigDecimal;

/**
 * Servicio de dominio con la regla de negocio simulada de procesamiento:
 * <ul>
 *   <li>Monto &le; 1000 → {@link EstadoCobro#PROCESADO}</li>
 *   <li>Monto &gt; 1000 → {@link EstadoCobro#FALLIDO}</li>
 * </ul>
 * Es una clase pura, sin dependencias de frameworks.
 */
public class ReglaProcesamientoCobro {

    public static final BigDecimal UMBRAL = new BigDecimal("1000");

    public EstadoCobro evaluar(Monto monto) {
        return monto.valor().compareTo(UMBRAL) <= 0
                ? EstadoCobro.PROCESADO
                : EstadoCobro.FALLIDO;
    }
}
