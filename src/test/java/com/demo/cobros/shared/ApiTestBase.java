package com.demo.cobros.shared;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base para las pruebas de integración de la API: añade el token de seguridad
 * ({@code X-API-Token}) a todas las peticiones por defecto.
 */
public abstract class ApiTestBase {

    public static final String API_TOKEN = "test-token";

    @BeforeEach
    protected void configurarPeticionConToken() {
        RestAssured.requestSpecification =
                new RequestSpecBuilder().addHeader("X-API-Token", API_TOKEN).build();
    }

    @AfterEach
    protected void limpiarPeticion() {
        RestAssured.requestSpecification = null;
    }
}
