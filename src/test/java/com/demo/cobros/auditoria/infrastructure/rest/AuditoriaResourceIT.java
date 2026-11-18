package com.demo.cobros.auditoria.infrastructure.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.demo.cobros.auditoria.infrastructure.persistence.AuditoriaPanacheRepository;
import com.demo.cobros.clientes.infrastructure.persistence.ClientePanacheRepository;
import com.demo.cobros.cobros.infrastructure.persistence.CobroPanacheRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Pruebas de integración de la consulta de auditoría. */
@QuarkusTest
class AuditoriaResourceIT {

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
    void listaTodosLosEventos() {
        generarTresEventos();

        given().when().get("/auditoria").then().statusCode(200).body("$", hasSize(3));
    }

    @Test
    void filtraPorEvento() {
        generarTresEventos();

        given().when()
                .get("/auditoria?evento=COBRO_PROCESADO")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].evento", equalTo("COBRO_PROCESADO"));
    }

    @Test
    void respetaElLimiteDeResultados() {
        generarTresEventos();

        given().when().get("/auditoria?limite=2").then().statusCode(200).body("$", hasSize(2));
    }

    @Test
    void eventoInvalidoDevuelve400() {
        given().when().get("/auditoria?evento=INVENTADO").then().statusCode(400).body("status", equalTo(400));
    }

    @Test
    void limiteInvalidoDevuelve400() {
        given().when().get("/auditoria?limite=0").then().statusCode(400).body("status", equalTo(400));
    }

    /** Genera los eventos CLIENTE_CREADO, COBRO_REGISTRADO y COBRO_PROCESADO. */
    private void generarTresEventos() {
        long clienteId =
                given().contentType(ContentType.JSON)
                        .body(Map.of("nombre", "Ana Pérez", "dpi", "1234567890123"))
                        .when()
                        .post("/clientes")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        long cobroId =
                given().contentType(ContentType.JSON)
                        .body(Map.of("clienteId", clienteId, "monto", 500.00, "moneda", "GTQ"))
                        .when()
                        .post("/cobros")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath()
                        .getLong("id");

        given().when().post("/cobros/" + cobroId + "/procesar").then().statusCode(200);
    }
}
