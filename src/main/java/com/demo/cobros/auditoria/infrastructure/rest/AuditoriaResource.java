package com.demo.cobros.auditoria.infrastructure.rest;

import com.demo.cobros.auditoria.application.ConsultarAuditoriaUseCase;
import com.demo.cobros.auditoria.application.dto.AuditoriaResponse;
import com.demo.cobros.auditoria.domain.model.EventoAuditoria;
import com.demo.cobros.shared.web.ParametrosConsulta;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Adaptador de entrada (REST) para consultar la auditoría del sistema.
 * Filtros opcionales: {@code evento}, {@code desde}, {@code hasta} y {@code limite}.
 */
@Path("/auditoria")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Auditoría")
public class AuditoriaResource {

    private static final int LIMITE_POR_DEFECTO = 100;

    @Inject ConsultarAuditoriaUseCase consultarAuditoria;

    @GET
    public List<AuditoriaResponse> listar(
            @QueryParam("evento") String evento,
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta,
            @QueryParam("limite") String limite) {
        return consultarAuditoria.ejecutar(
                ParametrosConsulta.parseEnum(EventoAuditoria.class, evento, "evento"),
                ParametrosConsulta.parseFecha(desde, "desde", false),
                ParametrosConsulta.parseFecha(hasta, "hasta", true),
                ParametrosConsulta.parseEnteroPositivo(limite, "limite", LIMITE_POR_DEFECTO));
    }
}
