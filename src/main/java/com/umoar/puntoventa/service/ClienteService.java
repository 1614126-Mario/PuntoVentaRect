package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Retorna el listado completo de clientes.
     *
     * @return Lista de clientes.
     */
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su identificador primario.
     *
     * @param id Identificador del cliente.
     * @return {@link Optional} con el cliente correspondiente.
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Busca un cliente a partir de su documento de identidad.
     *
     * @param documento Documento del cliente (DUI, NIT, etc.).
     * @return {@link Optional} con el cliente encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento);
    }

    /**
     * Registra o actualiza la información de un cliente.
     *
     * @param cliente Datos del cliente a persistir.
     * @return Cliente guardado.
     */
    @Transactional
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id Identificador del cliente a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    /**
     * Verifica si existe un cliente con el identificador proporcionado.
     *
     * @param id Identificador a verificar.
     * @return {@code true} si existe, {@code false} de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return clienteRepository.existsById(id);
    }
}
