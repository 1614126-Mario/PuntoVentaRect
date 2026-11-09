package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link DetalleVenta}.
 * Gestiona el almacenamiento y consulta de los renglones de venta.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    /**
     * Obtiene todos los renglones o productos asociados a una venta dada.
     *
     * @param ventaId Identificador de la venta.
     * @return Lista de detalles asociados a dicha venta.
     */
    List<DetalleVenta> findByVentaId(Long ventaId);
}
