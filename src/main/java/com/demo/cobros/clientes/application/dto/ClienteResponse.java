package com.demo.cobros.clientes.application.dto;

import com.demo.cobros.clientes.domain.model.Cliente;

/**
 * Representación de un cliente para las respuestas de la API.
 */
public record ClienteResponse(Long id, String nombre, String dpi, String email, String telefono) {

    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(
                cliente.id(),
                cliente.nombre(),
                cliente.dpi().valor(),
                cliente.email(),
                cliente.telefono());
    }
}
