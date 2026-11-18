package com.demo.cobros.cobros.domain.model;

import com.demo.cobros.cobros.domain.exception.MontoInvalidoException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object que representa el monto de un cobro: valor monetario positivo y
 * su moneda (ISO 4217, 3 letras).
 */
public record Monto(BigDecimal valor, String moneda) {

    public Monto {
        Objects.requireNonNull(valor, "El monto es obligatorio");
        if (valor.signum() <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a 0");
        }
        Objects.requireNonNull(moneda, "La moneda es obligatoria");
        String normalizada = moneda.trim().toUpperCase();
        if (normalizada.length() != 3) {
            throw new MontoInvalidoException(
                    "La moneda debe tener 3 letras (código ISO 4217), se recibió '%s'".formatted(moneda));
        }
        moneda = normalizada;
    }
}
