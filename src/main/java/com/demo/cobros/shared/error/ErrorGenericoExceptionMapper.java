package com.demo.cobros.shared.error;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Última red de seguridad: cualquier excepción no controlada se registra y se
 * responde como HTTP 500 sin filtrar detalles internos.
 */
@Provider
public class ErrorGenericoExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(ErrorGenericoExceptionMapper.class);

    @Context UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable excepcion) {
        // Las excepciones web (404 de ruta, 405, 415, etc.) conservan su respuesta.
        if (excepcion instanceof WebApplicationException webApplicationException) {
            return webApplicationException.getResponse();
        }

        LOG.error("Error no controlado procesando la petición", excepcion);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        ErrorResponse.de(
                                500,
                                "Internal Server Error",
                                "Ocurrió un error inesperado",
                                uriInfo.getRequestUri().getPath()))
                .build();
    }
}
