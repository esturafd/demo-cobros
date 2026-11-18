package com.demo.cobros.shared.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Registro básico de logs por request: método, ruta, estado y duración.
 * Además propaga un identificador de correlación {@code X-Request-Id}
 * (se reutiliza el entrante o se genera uno nuevo) y lo devuelve en la respuesta.
 * <p>Se ejecuta con la prioridad más alta para registrar también las peticiones
 * rechazadas (p. ej. 401).
 */
@Provider
@Priority(1)
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(RequestLoggingFilter.class);
    private static final String PROPIEDAD_INICIO = "cobros.request.inicio";
    private static final String PROPIEDAD_REQUEST_ID = "cobros.request.id";
    private static final String CABECERA_REQUEST_ID = "X-Request-Id";

    @Override
    public void filter(ContainerRequestContext contexto) {
        contexto.setProperty(PROPIEDAD_INICIO, System.nanoTime());

        String requestId = contexto.getHeaderString(CABECERA_REQUEST_ID);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        contexto.setProperty(PROPIEDAD_REQUEST_ID, requestId);
    }

    @Override
    public void filter(
            ContainerRequestContext contexto, ContainerResponseContext respuesta) {
        Long inicio = (Long) contexto.getProperty(PROPIEDAD_INICIO);
        long duracionMs = inicio == null ? 0L : (System.nanoTime() - inicio) / 1_000_000L;

        String requestId = (String) contexto.getProperty(PROPIEDAD_REQUEST_ID);
        if (requestId != null) {
            respuesta.getHeaders().add(CABECERA_REQUEST_ID, requestId);
        }

        LOG.infof(
                "%s %s -> %d (%d ms) [requestId=%s]",
                contexto.getMethod(),
                contexto.getUriInfo().getRequestUri().getPath(),
                respuesta.getStatus(),
                duracionMs,
                requestId);
    }
}
