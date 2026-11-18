package com.demo.cobros.cobros.application.dto;

import java.util.List;

/**
 * Resumen del procesamiento de un lote de cobros.
 */
public record ResumenLoteResponse(
        int total,
        int procesados,
        int fallidos,
        int omitidos,
        int noEncontrados,
        List<ResultadoCobroLote> detalle) {}
