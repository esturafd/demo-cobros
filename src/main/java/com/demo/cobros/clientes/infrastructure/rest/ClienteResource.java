package com.demo.cobros.clientes.infrastructure.rest;

import com.demo.cobros.clientes.application.CrearClienteUseCase;
import com.demo.cobros.clientes.application.ObtenerClienteUseCase;
import com.demo.cobros.clientes.application.dto.ClienteResponse;
import com.demo.cobros.clientes.application.dto.CrearClienteCommand;
import com.demo.cobros.cobros.application.ConsultarCobrosPorClienteUseCase;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.FiltroCobros;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.shared.web.ParametrosConsulta;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Adaptador de entrada (REST) para clientes. Incluye la consulta de los cobros
 * de un cliente, que delega en el caso de uso correspondiente del feature
 * {@code cobros}.
 */
@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes")
public class ClienteResource {

    @Inject CrearClienteUseCase crearCliente;
    @Inject ObtenerClienteUseCase obtenerCliente;
    @Inject ConsultarCobrosPorClienteUseCase consultarCobros;

    @ConfigProperty(name = "app.security.system-user", defaultValue = "sistema")
    String usuarioPorDefecto;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crear(
            @Valid CrearClienteCommand comando, @HeaderParam("X-System-User") String usuario) {
        ClienteResponse respuesta = crearCliente.ejecutar(comando, usuarioSistema(usuario));
        return Response.created(URI.create("/clientes/" + respuesta.id()))
                .entity(respuesta)
                .build();
    }

    @GET
    @Path("/{id}")
    public ClienteResponse obtener(@PathParam("id") Long id) {
        return obtenerCliente.ejecutar(id);
    }

    @GET
    @Path("/{id}/cobros")
    public List<CobroResponse> cobrosDeCliente(
            @PathParam("id") Long id,
            @QueryParam("estado") String estado,
            @QueryParam("desde") String desde,
            @QueryParam("hasta") String hasta) {
        FiltroCobros filtro =
                new FiltroCobros(
                        ParametrosConsulta.parseEnum(EstadoCobro.class, estado, "estado"),
                        ParametrosConsulta.parseFecha(desde, "desde", false),
                        ParametrosConsulta.parseFecha(hasta, "hasta", true));
        return consultarCobros.ejecutar(id, filtro);
    }

    private String usuarioSistema(String usuario) {
        return (usuario == null || usuario.isBlank()) ? usuarioPorDefecto : usuario;
    }
}
