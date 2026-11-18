package com.demo.cobros.shared.security;

import com.demo.cobros.shared.error.ErrorResponse;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Valida un token estático en la cabecera {@code X-API-Token}. Si falta o no
 * coincide, aborta la petición con HTTP 401 en el formato de error uniforme.
 * <p>Los endpoints de infraestructura bajo {@code /q/*} quedan exentos.
 * <p><strong>Alternativa:</strong> en producción se recomienda JWT/OIDC
 * (quarkus-oidc) en lugar de un token estático.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiTokenFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(ApiTokenFilter.class);
    private static final String CABECERA = "X-API-Token";

    @ConfigProperty(name = "app.security.api-token")
    String tokenConfigurado;

    @Override
    public void filter(ContainerRequestContext contexto) {
        String ruta = contexto.getUriInfo().getRequestUri().getPath();
        if (ruta.startsWith("/q/")) {
            return;
        }

        String token = contexto.getHeaderString(CABECERA);
        if (token == null || !coincide(token, tokenConfigurado)) {
            LOG.warnf("Acceso no autorizado a %s (token ausente o inválido)", ruta);
            contexto.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(
                                    ErrorResponse.de(
                                            401,
                                            "Unauthorized",
                                            "Token de API ausente o inválido",
                                            ruta))
                            .build());
        }
    }

    /** Comparación en tiempo constante para evitar ataques de temporización. */
    private static boolean coincide(String recibido, String esperado) {
        return MessageDigest.isEqual(
                recibido.getBytes(StandardCharsets.UTF_8),
                esperado.getBytes(StandardCharsets.UTF_8));
    }
}
