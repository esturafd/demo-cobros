package com.demo.cobros.clientes.infrastructure.rest;

import com.demo.cobros.clientes.application.CrearClienteUseCase;
import com.demo.cobros.clientes.application.ObtenerClienteUseCase;
import com.demo.cobros.clientes.application.dto.ClienteResponse;
import com.demo.cobros.clientes.application.dto.CrearClienteCommand;
import com.demo.cobros.cobros.application.ConsultarCobrosPorClienteUseCase;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.FiltroCobros;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.shared.error.ParametroInvalidoException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
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
                        parseEstado(estado),
                        parseFecha(desde, "desde", false),
                        parseFecha(hasta, "hasta", true));
        return consultarCobros.ejecutar(id, filtro);
    }

    private String usuarioSistema(String usuario) {
        return (usuario == null || usuario.isBlank()) ? usuarioPorDefecto : usuario;
    }

    private static EstadoCobro parseEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoCobro.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException excepcion) {
            throw new ParametroInvalidoException(
                    "estado", estado, "valores válidos: PENDIENTE, PROCESADO, FALLIDO");
        }
    }

    /**
     * Acepta un instante ISO-8601 ({@code 2026-01-31T23:59:59Z}) o una fecha
     * ({@code 2026-01-31}); en el segundo caso se usa el inicio (o el fin) del día UTC.
     */
    private static Instant parseFecha(String valor, String parametro, boolean finDelDia) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(valor);
        } catch (DateTimeParseException instanteInvalido) {
            try {
                LocalDate fecha = LocalDate.parse(valor);
                Instant inicioDia = fecha.atStartOfDay(ZoneOffset.UTC).toInstant();
                return finDelDia
                        ? inicioDia.plusSeconds(86_399).plusNanos(999_999_999)
                        : inicioDia;
            } catch (DateTimeParseException fechaInvalida) {
                throw new ParametroInvalidoException(
                        parametro, valor, "formato ISO-8601 esperado, p. ej. 2026-01-31 o 2026-01-31T23:59:59Z");
            }
        }
    }
}
