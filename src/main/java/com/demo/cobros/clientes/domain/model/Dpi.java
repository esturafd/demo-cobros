package com.demo.cobros.clientes.domain.model;

import com.demo.cobros.clientes.domain.exception.DpiInvalidoException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object que representa el DPI (Documento Personal de Identificación).
 * Regla de negocio: debe estar compuesto por 13 dígitos.
 */
public record Dpi(String valor) {

    private static final Pattern FORMATO = Pattern.compile("\\d{13}");

    public Dpi {
        Objects.requireNonNull(valor, "El DPI es obligatorio");
        String normalizado = valor.trim();
        if (!FORMATO.matcher(normalizado).matches()) {
            throw new DpiInvalidoException(normalizado);
        }
        valor = normalizado;
    }
}
