package com.demo.cobros.shared.error;

import com.demo.cobros.clientes.domain.exception.ClienteNoEncontradoException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapea {@link ClienteNoEncontradoException} a HTTP 404. */
@Provider
public class ClienteNoEncontradoExceptionMapper
        implements ExceptionMapper<ClienteNoEncontradoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(ClienteNoEncontradoException excepcion) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(
                        ErrorResponse.de(
                                404, "Not Found", excepcion.getMessage(), uriInfo.getRequestUri().getPath()))
                .build();
    }
}
