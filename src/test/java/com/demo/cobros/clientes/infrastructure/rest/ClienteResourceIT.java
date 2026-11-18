package com.demo.cobros.clientes.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.demo.cobros.auditoria.infrastructure.persistence.AuditoriaPanacheRepository;
import com.demo.cobros.clientes.infrastructure.persistence.ClientePanacheRepository;
import com.demo.cobros.cobros.infrastructure.persistence.CobroPanacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Pruebas de integración del recurso de clientes y de la consulta de sus cobros. */
@QuarkusTest
class ClienteResourceIT {

    @Inject ClientePanacheRepository clienteRepository;
    @Inject CobroPanacheRepository cobroRepository;
    @Inject AuditoriaPanacheRepository auditoriaRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        cobroRepository.deleteAll();
        auditoriaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void creaClienteCon201YLocation() {
        given().contentType(ContentType.JSON)
                .body(
                        Map.of(
                                "nombre", "Ana Pérez",
                                "dpi", "1234567890123",
                                "email", "ana@example.com"))
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .header("Location", containsString("/clientes/"))
                .body("dpi", equalTo("1234567890123"))
                .body("email", equalTo("ana@example.com"));
    }

    @Test
    void rechazaDpiDuplicadoCon409() {
        crearCliente("1234567890123");

        given().contentType(ContentType.JSON)
                .body(Map.of("nombre", "Otra Persona", "dpi", "1234567890123"))
                .when()
                .post("/clientes")
                .then()
                .statusCode(409)
                .body("status", equalTo(409));
    }

    @Test
    void rechazaDpiInvalidoCon400() {
        given().contentType(ContentType.JSON)
                .body(Map.of("nombre", "Ana", "dpi", "123"))
                .when()
                .post("/clientes")
                .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void obtieneClientePorId() {
        long id = crearCliente("1234567890123");

        given().when()
                .get("/clientes/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo((int) id))
                .body("nombre", equalTo("Ana Pérez"));
    }

    @Test
    void clienteInexistenteDevuelve404() {
        given().when().get("/clientes/999999").then().statusCode(404);
    }

    @Test
    void consultaCobrosFiltrandoPorEstado() {
        long clienteId = crearCliente("1234567890123");
        long procesado = registrarCobro(clienteId, 500.00);
        registrarCobro(clienteId, 800.00);

        given().when().post("/cobros/" + procesado + "/procesar").then().statusCode(200);

        given().when()
                .get("/clientes/" + clienteId + "/cobros")
                .then()
                .statusCode(200)
                .body("$", hasSize(2));

        given().when()
                .get("/clientes/" + clienteId + "/cobros?estado=PROCESADO")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].estado", equalTo("PROCESADO"));

        given().when()
                .get("/clientes/" + clienteId + "/cobros?estado=PENDIENTE")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].estado", equalTo("PENDIENTE"));
    }

    @Test
    void consultaCobrosPorRangoDeFechas() {
        long clienteId = crearCliente("1234567890123");
        registrarCobro(clienteId, 500.00);

        String hoy = LocalDate.now(ZoneOffset.UTC).toString();

        given().when()
                .get("/clientes/" + clienteId + "/cobros?desde=" + hoy + "&hasta=" + hoy)
                .then()
                .statusCode(200)
                .body("$", hasSize(1));

        given().when()
                .get("/clientes/" + clienteId + "/cobros?desde=2000-01-01&hasta=2000-12-31")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));
    }

    @Test
    void estadoInvalidoDevuelve400() {
        long clienteId = crearCliente("1234567890123");

        given().when()
                .get("/clientes/" + clienteId + "/cobros?estado=INVENTADO")
                .then()
                .statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void consultaCobrosDeClienteInexistenteDevuelve404() {
        given().when().get("/clientes/999999/cobros").then().statusCode(404);
    }

    private long crearCliente(String dpi) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("nombre", "Ana Pérez", "dpi", dpi))
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    private long registrarCobro(long clienteId, double monto) {
        return given().contentType(ContentType.JSON)
                .body(Map.of("clienteId", clienteId, "monto", monto, "moneda", "GTQ"))
                .when()
                .post("/cobros")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }
}
