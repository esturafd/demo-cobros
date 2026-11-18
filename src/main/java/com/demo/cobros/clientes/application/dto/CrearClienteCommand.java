package com.demo.cobros.clientes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para crear un cliente.
 */
public record CrearClienteCommand(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El DPI es obligatorio")
                @Pattern(regexp = "\\d{13}", message = "El DPI debe tener 13 dígitos")
                String dpi,
        @Email(message = "El email no tiene un formato válido") String email,
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres") String telefono) {}
