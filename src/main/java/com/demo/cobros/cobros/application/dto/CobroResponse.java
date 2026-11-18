package com.demo.cobros.cobros.application.dto;

import com.demo.cobros.cobros.domain.model.Cobro;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representación de un cobro para las respuestas de la API.
 */
public record CobroResponse(
        Long id,
        Long clienteId,
        BigDecimal monto,
        String moneda,
        String estado,
        Instant fechaCreacion,
        Instant fechaProceso,
        String referenciaExterna) {

    public static CobroResponse desde(Cobro cobro) {
        return new CobroResponse(
                cobro.id(),
                cobro.clienteId(),
                cobro.monto().valor(),
                cobro.monto().moneda(),
                cobro.estado().name(),
                cobro.fechaCreacion(),
                cobro.fechaProceso(),
                cobro.referenciaExterna());
    }
}
