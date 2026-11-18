package com.demo.cobros.shared.error;

import com.demo.cobros.clientes.domain.exception.DpiInvalidoException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapea {@link DpiInvalidoException} a HTTP 400. */
@Provider
public class DpiInvalidoExceptionMapper implements ExceptionMapper<DpiInvalidoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(DpiInvalidoException excepcion) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        ErrorResponse.de(
                                400, "Bad Request", excepcion.getMessage(), uriInfo.getRequestUri().getPath()))
                .build();
    }
}
