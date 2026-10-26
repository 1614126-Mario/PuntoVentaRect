package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByClienteId(Long clienteId);
    List<Venta> findAllByOrderByFechaVentaDesc();
    List<Venta> findByTipoPedidoOrderByFechaVentaDesc(String tipoPedido);
    Optional<Venta> findFirstByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);
}
