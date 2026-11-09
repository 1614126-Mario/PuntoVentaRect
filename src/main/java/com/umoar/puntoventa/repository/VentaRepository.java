package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Venta}.
 * Proporciona métodos de consulta para el historial de ventas, comandas y pedidos en línea.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /**
     * Busca las ventas realizadas por un cliente específico.
     *
     * @param clienteId Identificador del cliente.
     * @return Lista de ventas asociadas a dicho cliente.
     */
    List<Venta> findByClienteId(Long clienteId);

    /**
     * Obtiene todas las ventas ordenadas descendentemente por fecha.
     *
     * @return Lista ordenada de todas las ventas.
     */
    List<Venta> findAllByOrderByFechaVentaDesc();

    /**
     * Obtiene las ventas según el tipo de pedido (LOCAL, EN_LINEA, LLEVAR) ordenadas descendentemente por fecha.
     *
     * @param tipoPedido Modalidad o tipo de pedido.
     * @return Lista de ventas coincidentes.
     */
    List<Venta> findByTipoPedidoOrderByFechaVentaDesc(String tipoPedido);

    /**
     * Busca la primera venta que coincida con el número o folio de factura.
     *
     * @param numeroFactura Folio correlativo de factura o pedido.
     * @return {@link Optional} con la venta encontrada.
     */
    Optional<Venta> findFirstByNumeroFactura(String numeroFactura);

    /**
     * Comprueba si ya existe una venta registrada con un determinado número de factura.
     *
     * @param numeroFactura Folio a verificar.
     * @return {@code true} si ya existe, {@code false} de lo contrario.
     */
    boolean existsByNumeroFactura(String numeroFactura);
}
