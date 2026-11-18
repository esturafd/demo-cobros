package com.demo.cobros.shared.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

/**
 * Normaliza los errores de Bean Validation a HTTP 400 con el mismo formato de
 * error que el resto de la API.
 */
@Provider
public class ValidacionExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException excepcion) {
        List<ErrorResponse.DetalleError> detalles =
                excepcion.getConstraintViolations().stream()
                        .map(ValidacionExceptionMapper::aDetalle)
                        .toList();

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

    private static ErrorResponse.DetalleError aDetalle(ConstraintViolation<?> violacion) {
        return new ErrorResponse.DetalleError(
                violacion.getPropertyPath().toString(), violacion.getMessage());
    }
}
