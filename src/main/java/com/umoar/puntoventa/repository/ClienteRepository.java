package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Cliente}.
 * Gestiona consultas y operaciones de persistencia de clientes.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un cliente a partir de su documento único de identidad (DUI, NIT, etc.).
     *
     * @param documento Número o código de documento del cliente.
     * @return {@link Optional} con el cliente si existe, o vacío en caso contrario.
     */
    Optional<Cliente> findByDocumento(String documento);
}
