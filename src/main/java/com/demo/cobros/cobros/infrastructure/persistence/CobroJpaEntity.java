package com.demo.cobros.cobros.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entidad JPA del cobro. Detalle de infraestructura: el modelo de dominio
 * ({@code cobros.domain.model.Cobro}) es independiente de esta clase.
 */
@Entity
@Table(
        name = "cobro",
        uniqueConstraints =
                @UniqueConstraint(name = "uk_cobro_referencia", columnNames = "referencia_externa"),
        indexes = {
            @Index(name = "ix_cobro_cliente", columnList = "cliente_id"),
            @Index(name = "ix_cobro_estado", columnList = "estado"),
            @Index(name = "ix_cobro_fecha_creacion", columnList = "fecha_creacion")
        })
public class CobroJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "cliente_id", nullable = false)
    public Long clienteId;

    @Column(name = "monto", nullable = false, precision = 19, scale = 2)
    public BigDecimal monto;

    @Column(name = "moneda", nullable = false, length = 3)
    public String moneda;

    @Column(name = "estado", nullable = false, length = 20)
    public String estado;

    @Column(name = "fecha_creacion", nullable = false)
    public Instant fechaCreacion;

    @Column(name = "fecha_proceso")
    public Instant fechaProceso;

    @Column(name = "referencia_externa", nullable = false, length = 64)
    public String referenciaExterna;
}
