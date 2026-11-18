package com.demo.cobros.clientes.domain.exception;

/**
 * Se lanza cuando no se encuentra un cliente por su identificador.
 * El mapeo a HTTP 404 se realiza en la capa compartida de errores.
 */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long id) {
        super("No existe el cliente con id %d".formatted(id));
    }
}
