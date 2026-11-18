package com.demo.cobros.cobros.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Datos de entrada para registrar un cobro.
 */
public record RegistrarCobroCommand(
        @NotNull(message = "El clienteId es obligatorio") Long clienteId,
        @NotNull(message = "El monto es obligatorio")
                @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
                BigDecimal monto,
        @NotBlank(message = "La moneda es obligatoria")
                @Size(min = 3, max = 3, message = "La moneda debe tener 3 letras (ISO 4217)")
                String moneda) {}
