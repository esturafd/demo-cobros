package com.demo.cobros.cobros.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.demo.cobros.auditoria.infrastructure.persistence.AuditoriaPanacheRepository;
import com.demo.cobros.clientes.domain.model.Cliente;
import com.demo.cobros.clientes.infrastructure.persistence.ClientePanacheRepository;
import com.demo.cobros.cobros.infrastructure.persistence.CobroPanacheRepository;
import com.demo.cobros.shared.ApiTestBase;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Pruebas de integración del recurso de cobros (REST + persistencia SQLite). */
@QuarkusTest
class CobroResourceIT extends ApiTestBase {

    @Inject ClientePanacheRepository clienteRepository;
    @Inject CobroPanacheRepository cobroRepository;
    @Inject AuditoriaPanacheRepository auditoriaRepository;

    private Long clienteId;

    @BeforeEach
    @Transactional
    void setUp() {
        cobroRepository.deleteAll();
        auditoriaRepository.deleteAll();
        clienteRepository.deleteAll();
        clienteId =
                clienteRepository
                        .guardar(Cliente.nuevo("Ana Pérez", "1234567890123", null, null))
                        .id();
    }

    @Test
    void registraCobroEnEstadoPendiente() {
        given().contentType(ContentType.JSON)
                .body(Map.of("clienteId", clienteId, "monto", 500.00, "moneda", "GTQ"))
                .when()
                .post("/cobros")
                .then()
                .statusCode(201)
                .body("estado", equalTo("PENDIENTE"))
                .body("referenciaExterna", notNullValue());
    }

    @Test
    void rechazaMontoInvalidoCon400() {
        given().contentType(ContentType.JSON)
                .body(Map.of("clienteId", clienteId, "monto", 0, "moneda", "GTQ"))
                .when()
                .post("/cobros")
                .then()
                .statusCode(400);
    }

    @Test
    void rechazaClienteInexistenteCon404() {
        given().contentType(ContentType.JSON)
                .body(Map.of("clienteId", 999999, "monto", 100, "moneda", "GTQ"))
                .when()
                .post("/cobros")
                .then()
                .statusCode(404)
                .body("message", containsString("999999"));
    }

    @Test
    void procesaCobroMenorIgualAlUmbral() {
        long id = registrar(500.00);

        given().when()
                .post("/cobros/" + id + "/procesar")
                .then()
                .statusCode(200)
                .body("estado", equalTo("PROCESADO"))
                .body("fechaProceso", notNullValue());
    }

    @Test
    void procesaCobroMayorAlUmbralComoFallido() {
        long id = registrar(1500.00);

        given().when()
                .post("/cobros/" + id + "/procesar")
                .then()
                .statusCode(200)
                .body("estado", equalTo("FALLIDO"));
    }

    @Test
    void procesarCobroInexistenteDevuelve404() {
        given().when().post("/cobros/999999/procesar").then().statusCode(404);
    }

    @Test
    void procesaLoteDeFormaIdempotente() {
        long ok = registrar(500.00);
        long falla = registrar(1500.00);

        given().contentType(ContentType.JSON)
                .body(Map.of("cobroIds", List.of(ok, falla, 999999)))
                .when()
                .post("/cobros/lotes/procesar")
                .then()
                .statusCode(200)
                .body("total", equalTo(3))
                .body("procesados", equalTo(1))
                .body("fallidos", equalTo(1))
                .body("noEncontrados", equalTo(1));

        // Reenvío del mismo lote: idempotente, todo omitido.
        given().contentType(ContentType.JSON)
                .body(Map.of("cobroIds", List.of(ok, falla)))
                .when()
                .post("/cobros/lotes/procesar")
                .then()
                .statusCode(200)
                .body("procesados", equalTo(0))
                .body("fallidos", equalTo(0))
                .body("omitidos", equalTo(2));
    }

    @Test
    void rechazaLoteVacioCon400() {
        given().contentType(ContentType.JSON)
                .body(Map.of("cobroIds", List.of()))
                .when()
                .post("/cobros/lotes/procesar")
                .then()
                .statusCode(400);
    }

    @Test
    void registraAuditoriaDeRegistroYProcesamiento() {
        long id = registrar(500.00);

        given().when().post("/cobros/" + id + "/procesar").then().statusCode(200);

        long eventos = QuarkusTransaction.requiringNew().call(() -> auditoriaRepository.count());
        assertEquals(2, eventos);
    }

    private long registrar(double monto) {
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
