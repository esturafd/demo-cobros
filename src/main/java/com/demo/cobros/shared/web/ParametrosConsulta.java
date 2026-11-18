package com.demo.cobros.shared.web;

import com.demo.cobros.shared.error.ParametroInvalidoException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utilidades para interpretar parámetros de query string (enums, fechas y
 * enteros) de forma homogénea, lanzando {@link ParametroInvalidoException}
 * (HTTP 400) cuando el valor no es válido.
 */
public final class ParametrosConsulta {

    private ParametrosConsulta() {}

    /** Interpreta un enum por nombre (sin distinguir mayúsculas). */
    public static <E extends Enum<E>> E parseEnum(Class<E> tipo, String valor, String parametro) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(tipo, valor.trim().toUpperCase());
        } catch (IllegalArgumentException excepcion) {
            String validos =
                    Arrays.stream(tipo.getEnumConstants())
                            .map(Enum::name)
                            .collect(Collectors.joining(", "));
            throw new ParametroInvalidoException(parametro, valor, "valores válidos: " + validos);
        }
    }

    /**
     * Interpreta un instante ISO-8601 ({@code 2026-01-31T23:59:59Z}) o una fecha
     * ({@code 2026-01-31}); en el segundo caso usa el inicio (o el fin) del día UTC.
     */
    public static Instant parseFecha(String valor, String parametro, boolean finDelDia) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(valor);
        } catch (DateTimeParseException instanteInvalido) {
            try {
                LocalDate fecha = LocalDate.parse(valor);
                Instant inicioDia = fecha.atStartOfDay(ZoneOffset.UTC).toInstant();
                return finDelDia ? inicioDia.plusSeconds(86_399).plusNanos(999_999_999) : inicioDia;
            } catch (DateTimeParseException fechaInvalida) {
                throw new ParametroInvalidoException(
                        parametro,
                        valor,
                        "formato ISO-8601 esperado, p. ej. 2026-01-31 o 2026-01-31T23:59:59Z");
            }
        }
    }

    /** Interpreta un entero positivo, con valor por defecto si viene vacío. */
    public static int parseEnteroPositivo(String valor, String parametro, int porDefecto) {
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        try {
            int numero = Integer.parseInt(valor.trim());
            if (numero <= 0) {
                throw new NumberFormatException("no positivo");
            }
            return numero;
        } catch (NumberFormatException excepcion) {
            throw new ParametroInvalidoException(parametro, valor, "se espera un entero positivo");
        }
    }
}
