package com.demo.cobros.cobros.application.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Datos de entrada para procesar un lote de cobros.
 */
public record ProcesarLoteCommand(
        @NotEmpty(message = "La lista de cobros no puede estar vacía") List<Long> cobroIds) {}
