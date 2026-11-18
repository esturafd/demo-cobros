package com.demo.cobros.clientes.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio {@code Cliente}. Inmutable y libre de frameworks:
 * no conoce JPA, CDI ni HTTP. El identificador es {@code null} hasta que la
 * entidad es persistida.
 */
public class Cliente {

    private final Long id;
    private final String nombre;
    private final Dpi dpi;
    private final String email;
    private final String telefono;

    private Cliente(Long id, String nombre, Dpi dpi, String email, String telefono) {
        this.id = id;
        this.nombre = validarNombre(nombre);
        this.dpi = Objects.requireNonNull(dpi, "El DPI es obligatorio");
        this.email = normalizarOpcional(email);
        this.telefono = normalizarOpcional(telefono);
    }

    /** Crea un cliente nuevo (aún sin identificador). */
    public static Cliente nuevo(String nombre, String dpi, String email, String telefono) {
        return new Cliente(null, nombre, new Dpi(dpi), email, telefono);
    }

    /** Reconstruye un cliente ya persistido a partir de sus datos almacenados. */
    public static Cliente reconstituir(Long id, String nombre, String dpi, String email, String telefono) {
        return new Cliente(id, nombre, new Dpi(dpi), email, telefono);
    }

    /** Devuelve una copia con el identificador asignado tras la persistencia. */
    public Cliente conId(Long nuevoId) {
        return new Cliente(nuevoId, nombre, dpi, email, telefono);
    }

    private static String validarNombre(String nombre) {
        Objects.requireNonNull(nombre, "El nombre es obligatorio");
        String normalizado = nombre.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return normalizado;
    }

    private static String normalizarOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.trim();
        return normalizado.isEmpty() ? null : normalizado;
    }

    public Long id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public Dpi dpi() {
        return dpi;
    }

    public String email() {
        return email;
    }

    public String telefono() {
        return telefono;
    }
}
