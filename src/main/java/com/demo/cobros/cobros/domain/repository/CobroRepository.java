package com.demo.cobros.cobros.domain.repository;

import com.demo.cobros.cobros.domain.model.Cobro;
import com.demo.cobros.cobros.domain.model.EstadoCobro;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de persistencia de cobros. La implementación concreta vive en
 * {@code cobros.infrastructure.persistence}.
 */
public interface CobroRepository {

    Cobro guardar(Cobro cobro);

    Optional<Cobro> buscarPorId(Long id);

    List<Cobro> buscarPorIds(Collection<Long> ids);

    /**
     * Consulta los cobros de un cliente aplicando filtros opcionales.
     *
     * @param clienteId cliente dueño de los cobros
     * @param estado    filtro por estado (opcional)
     * @param desde     fecha de creación mínima inclusive (opcional)
     * @param hasta     fecha de creación máxima inclusive (opcional)
     */
    List<Cobro> buscarPorCliente(Long clienteId, EstadoCobro estado, Instant desde, Instant hasta);
}
