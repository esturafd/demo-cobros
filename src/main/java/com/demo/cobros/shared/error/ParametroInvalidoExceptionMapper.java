package com.demo.cobros.shared.error;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

/** Mapea {@link ParametroInvalidoException} a HTTP 400. */
@Provider
public class ParametroInvalidoExceptionMapper
        implements ExceptionMapper<ParametroInvalidoException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(ParametroInvalidoException excepcion) {
        List<ErrorResponse.DetalleError> detalles =
                List.of(new ErrorResponse.DetalleError(excepcion.parametro(), excepcion.detalle()));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(
                        ErrorResponse.conDetalles(
                                400,
                                "Bad Request",
                                "Datos inválidos",
                                uriInfo.getRequestUri().getPath(),
                                detalles))
                .build();
    }
}
