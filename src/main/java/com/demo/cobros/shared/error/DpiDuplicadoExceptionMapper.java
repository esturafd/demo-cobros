package com.demo.cobros.shared.error;

import com.demo.cobros.clientes.domain.exception.DpiDuplicadoException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Mapea {@link DpiDuplicadoException} a HTTP 409. */
@Provider
public class DpiDuplicadoExceptionMapper implements ExceptionMapper<DpiDuplicadoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(DpiDuplicadoException excepcion) {
        return Response.status(Response.Status.CONFLICT)
                .entity(
                        ErrorResponse.de(
                                409, "Conflict", excepcion.getMessage(), uriInfo.getRequestUri().getPath()))
                .build();
    }
}
