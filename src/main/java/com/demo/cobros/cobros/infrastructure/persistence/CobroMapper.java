package com.demo.cobros.cobros.infrastructure.persistence;

import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import com.demo.cobros.cobros.domain.model.Monto;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Traduce entre el modelo de dominio y la entidad JPA del cobro.
 */
@ApplicationScoped
public class CobroMapper {

    public Cobro aDominio(CobroJpaEntity entidad) {
        return Cobro.reconstituir(
                entidad.id,
                entidad.clienteId,
                new Monto(entidad.monto, entidad.moneda),
                EstadoCobro.valueOf(entidad.estado),
                entidad.fechaCreacion,
                entidad.fechaProceso,
                entidad.referenciaExterna);
    }

    public CobroJpaEntity aJpa(Cobro cobro) {
        CobroJpaEntity entidad = new CobroJpaEntity();
        entidad.id = cobro.id();
        entidad.clienteId = cobro.clienteId();
        entidad.monto = cobro.monto().valor();
        entidad.moneda = cobro.monto().moneda();
        entidad.estado = cobro.estado().name();
        entidad.fechaCreacion = cobro.fechaCreacion();
        entidad.fechaProceso = cobro.fechaProceso();
        entidad.referenciaExterna = cobro.referenciaExterna();
        return entidad;
    }
}
