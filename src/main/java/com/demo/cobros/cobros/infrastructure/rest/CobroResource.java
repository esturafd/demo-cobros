package com.demo.cobros.cobros.infrastructure.rest;

import com.demo.cobros.cobros.application.ProcesarCobroUseCase;
import com.demo.cobros.cobros.application.ProcesarLoteCobrosUseCase;
import com.demo.cobros.cobros.application.RegistrarCobroUseCase;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.ProcesarLoteCommand;
import com.demo.cobros.cobros.application.dto.RegistrarCobroCommand;
import com.demo.cobros.cobros.application.dto.ResumenLoteResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Adaptador de entrada (REST) para los cobros. Delega toda la lógica en los
 * casos de uso de la capa de aplicación.
 */
@Path("/cobros")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Cobros")
public class CobroResource {

    @Inject RegistrarCobroUseCase registrarCobro;
    @Inject ProcesarCobroUseCase procesarCobro;
    @Inject ProcesarLoteCobrosUseCase procesarLote;

    @ConfigProperty(name = "app.security.system-user", defaultValue = "sistema")
    String usuarioPorDefecto;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(
            @Valid RegistrarCobroCommand comando, @HeaderParam("X-System-User") String usuario) {
        CobroResponse respuesta = registrarCobro.ejecutar(comando, usuarioSistema(usuario));
        return Response.created(URI.create("/cobros/" + respuesta.id()))
                .entity(respuesta)
                .build();
    }

    @POST
    @Path("/{id}/procesar")
    public CobroResponse procesar(
            @PathParam("id") Long id, @HeaderParam("X-System-User") String usuario) {
        return procesarCobro.ejecutar(id, usuarioSistema(usuario));
    }

    @POST
    @Path("/lotes/procesar")
    @Consumes(MediaType.APPLICATION_JSON)
    public ResumenLoteResponse procesarLote(
            @Valid ProcesarLoteCommand comando, @HeaderParam("X-System-User") String usuario) {
        return procesarLote.ejecutar(comando, usuarioSistema(usuario));
    }

    private String usuarioSistema(String usuario) {
        return (usuario == null || usuario.isBlank()) ? usuarioPorDefecto : usuario;
    }
}
