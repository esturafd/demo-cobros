package com.demo.cobros.cobros.application.dto;

/**
 * Resultado del procesamiento de un cobro dentro de un lote.
 *
 * @param id        identificador del cobro
 * @param estado    estado resultante (o {@code null} si no se encontró)
 * @param resultado resultado lógico: PROCESADO, FALLIDO, OMITIDO o NO_ENCONTRADO
 * @param mensaje   detalle opcional
 */
public record ResultadoCobroLote(Long id, String estado, String resultado, String mensaje) {

    public static ResultadoCobroLote procesado(Long id, String estado) {
        return new ResultadoCobroLote(id, estado, estado, null);
    }

    public static ResultadoCobroLote omitido(Long id, String estado) {
        return new ResultadoCobroLote(
                id, estado, "OMITIDO", "El cobro ya estaba en un estado terminal");
    }

    public static ResultadoCobroLote noEncontrado(Long id) {
        return new ResultadoCobroLote(id, null, "NO_ENCONTRADO", "No existe el cobro indicado");
    }
}
