package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Comercio}.
 * Proporciona métodos CRUD para los datos fiscales e institucionales del comercio.
 */
@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {
}
