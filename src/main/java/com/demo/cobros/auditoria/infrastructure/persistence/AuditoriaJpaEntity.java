package com.demo.cobros.auditoria.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Entidad JPA de auditoría. Detalle de infraestructura.
 */
@Entity
@Table(
        name = "auditoria",
        indexes = @Index(name = "ix_auditoria_fecha", columnList = "fecha"))
public class AuditoriaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "evento", nullable = false, length = 40)
    public String evento;

    @Column(name = "resumen_payload", length = 2000)
    public String resumenPayload;

    @Column(name = "fecha", nullable = false)
    public Instant fecha;

    @Column(name = "usuario_sistema", length = 80)
    public String usuarioSistema;
}
