package com.demo.cobros.clientes.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entidad JPA del cliente. Es un detalle de infraestructura: el modelo de dominio
 * ({@code clientes.domain.model.Cliente}) es independiente de esta clase.
 */
@Entity
@Table(
        name = "cliente",
        uniqueConstraints = @UniqueConstraint(name = "uk_cliente_dpi", columnNames = "dpi"))
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    public String nombre;

    @Column(name = "dpi", nullable = false, length = 13)
    public String dpi;

    @Column(name = "email", length = 160)
    public String email;

    @Column(name = "telefono", length = 20)
    public String telefono;
}
