package com.demo.cobros.shared.error;

import com.demo.cobros.cobros.domain.exception.MontoInvalidoException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapea {@link MontoInvalidoException} a HTTP 400. */
@Provider
public class MontoInvalidoExceptionMapper implements ExceptionMapper<MontoInvalidoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(MontoInvalidoException excepcion) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        ErrorResponse.de(
                                400, "Bad Request", excepcion.getMessage(), uriInfo.getRequestUri().getPath()))
                .build();
    }
}
