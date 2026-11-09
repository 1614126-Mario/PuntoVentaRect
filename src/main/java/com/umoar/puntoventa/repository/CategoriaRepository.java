package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Categoria}.
 * Permite operaciones CRUD sobre la tabla de categorías de productos.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
