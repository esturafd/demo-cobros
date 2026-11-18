package com.demo.cobros.cobros.application;

import com.demo.cobros.clientes.domain.exception.ClienteNoEncontradoException;
import com.demo.cobros.clientes.domain.repository.ClienteRepository;
import com.demo.cobros.cobros.application.dto.CobroResponse;
import com.demo.cobros.cobros.application.dto.FiltroCobros;
import com.demo.cobros.cobros.domain.repository.CobroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Caso de uso: consultar los cobros de un cliente con filtros opcionales
 * (estado y rango de fechas).
 */
@ApplicationScoped
public class ConsultarCobrosPorClienteUseCase {

    private final CobroRepository cobroRepository;
    private final ClienteRepository clienteRepository;

    @Inject
    public ConsultarCobrosPorClienteUseCase(
            CobroRepository cobroRepository, ClienteRepository clienteRepository) {
        this.cobroRepository = cobroRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public List<CobroResponse> ejecutar(Long clienteId, FiltroCobros filtro) {
        if (!clienteRepository.existePorId(clienteId)) {
            throw new ClienteNoEncontradoException(clienteId);
        }

        return cobroRepository
                .buscarPorCliente(clienteId, filtro.estado(), filtro.desde(), filtro.hasta())
                .stream()
                .map(CobroResponse::desde)
                .toList();
    }
}
