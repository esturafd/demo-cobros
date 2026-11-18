package com.demo.cobros.shared.error;

import com.demo.cobros.cobros.domain.exception.CobroNoEncontradoException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapea {@link CobroNoEncontradoException} a HTTP 404. */
@Provider
public class CobroNoEncontradoExceptionMapper
        implements ExceptionMapper<CobroNoEncontradoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(CobroNoEncontradoException excepcion) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(
                        ErrorResponse.de(
                                404, "Not Found", excepcion.getMessage(), uriInfo.getRequestUri().getPath()))
                .build();
    }
}
