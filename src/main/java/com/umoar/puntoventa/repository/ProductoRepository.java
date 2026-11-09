package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Producto}.
 * Gestiona el inventario y catálogo de productos.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su código de barras único.
     *
     * @param codigoBarras Código de barras o SKU del producto.
     * @return {@link Optional} con el producto correspondiente si existe.
     */
    Optional<Producto> findByCodigoBarras(String codigoBarras);
}
