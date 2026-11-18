package com.demo.cobros.cobros.application.dto;

import com.demo.cobros.cobros.domain.model.EstadoCobro;
import java.time.Instant;

/**
 * Filtros opcionales para consultar los cobros de un cliente.
 */
public record FiltroCobros(EstadoCobro estado, Instant desde, Instant hasta) {

    public static FiltroCobros vacio() {
        return new FiltroCobros(null, null, null);
    }
}
