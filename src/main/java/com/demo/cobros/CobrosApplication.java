package com.demo.cobros;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * Punto de entrada de la aplicación. Quarkus lo detecta por {@link QuarkusMain}
 * y lo usa tanto en modo dev como al ejecutar el jar empaquetado.
 */
@QuarkusMain
public class CobrosApplication implements QuarkusApplication {

    @Override
    public int run(String... args) {
        Quarkus.waitForExit();
        return 0;
    }
}
