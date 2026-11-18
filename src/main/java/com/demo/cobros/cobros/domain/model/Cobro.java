package com.demo.cobros.cobros.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio {@code Cobro}. Inmutable y libre de frameworks.
 * El estado evoluciona vía {@link #procesado(EstadoCobro, Instant)}, que devuelve
 * una nueva instancia.
 */
public class Cobro {

    private final Long id;
    private final Long clienteId;
    private final Monto monto;
    private final EstadoCobro estado;
    private final Instant fechaCreacion;
    private final Instant fechaProceso;
    private final String referenciaExterna;

    private Cobro(
            Long id,
            Long clienteId,
            Monto monto,
            EstadoCobro estado,
            Instant fechaCreacion,
            Instant fechaProceso,
            String referenciaExterna) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "El cliente es obligatorio");
        this.monto = Objects.requireNonNull(monto, "El monto es obligatorio");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación es obligatoria");
        this.fechaProceso = fechaProceso;
        this.referenciaExterna = Objects.requireNonNull(referenciaExterna, "La referencia externa es obligatoria");
    }

    /** Crea un cobro nuevo en estado PENDIENTE. */
    public static Cobro nuevo(
            Long clienteId, Monto monto, String referenciaExterna, Instant fechaCreacion) {
        return new Cobro(
                null, clienteId, monto, EstadoCobro.PENDIENTE, fechaCreacion, null, referenciaExterna);
    }

    /** Reconstruye un cobro ya persistido. */
    public static Cobro reconstituir(
            Long id,
            Long clienteId,
            Monto monto,
            EstadoCobro estado,
            Instant fechaCreacion,
            Instant fechaProceso,
            String referenciaExterna) {
        return new Cobro(id, clienteId, monto, estado, fechaCreacion, fechaProceso, referenciaExterna);
    }

    public Cobro conId(Long nuevoId) {
        return new Cobro(nuevoId, clienteId, monto, estado, fechaCreacion, fechaProceso, referenciaExterna);
    }

    /** Devuelve una copia con el estado resultante del procesamiento. */
    public Cobro procesado(EstadoCobro nuevoEstado, Instant fechaProceso) {
        return new Cobro(id, clienteId, monto, nuevoEstado, fechaCreacion, fechaProceso, referenciaExterna);
    }

    public boolean estaPendiente() {
        return estado == EstadoCobro.PENDIENTE;
    }

    public Long id() {
        return id;
    }

    public Long clienteId() {
        return clienteId;
    }

    public Monto monto() {
        return monto;
    }

    public EstadoCobro estado() {
        return estado;
    }

    public Instant fechaCreacion() {
        return fechaCreacion;
    }

    public Instant fechaProceso() {
        return fechaProceso;
    }

    public String referenciaExterna() {
        return referenciaExterna;
    }
}
