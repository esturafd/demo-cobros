package com.demo.cobros.shared.security;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.demo.cobros.shared.ApiTestBase;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

/** Pruebas del filtro de seguridad por token. */
@QuarkusTest
class ApiTokenFilterIT extends ApiTestBase {

    @Test
    void sinTokenDevuelve401() {
        RestAssured.requestSpecification = null;

        given().when()
                .get("/clientes/1")
                .then()
                .statusCode(401)
                .body("status", equalTo(401));
    }

    @Test
    void tokenInvalidoDevuelve401() {
        given().header("X-API-Token", "incorrecto")
                .when()
                .get("/clientes/1")
                .then()
                .statusCode(401)
                .body("status", equalTo(401));
    }

    @Test
    void tokenValidoPermiteElAcceso() {
        given().when().get("/auditoria").then().statusCode(200);
    }

    @Test
    void losEndpointsDeInfraestructuraNoRequierenToken() {
        RestAssured.requestSpecification = null;

        given().when().get("/q/health").then().statusCode(200);
    }
}
