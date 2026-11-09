package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de ventas, facturación y pedidos en línea.
 */
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;

    /**
     * Retorna todas las ventas ordenadas descendentemente por fecha.
     *
     * @return Lista de ventas.
     */
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByFechaVentaDesc();
    }

    /**
     * Retorna las ventas filtradas por modalidad o tipo de pedido (LOCAL, EN_LINEA, LLEVAR).
     *
     * @param tipoPedido Modalidad del pedido.
     * @return Lista de ventas del tipo solicitado.
     */
    @Transactional(readOnly = true)
    public List<Venta> findByTipoPedido(String tipoPedido) {
        return ventaRepository.findByTipoPedidoOrderByFechaVentaDesc(tipoPedido);
    }

    /**
     * Busca una venta por su identificador único.
     *
     * @param id Identificador de la venta.
     * @return {@link Optional} con la venta encontrada.
     */
    @Transactional(readOnly = true)
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    /**
     * Busca todas las ventas asociadas a un determinado cliente.
     *
     * @param clienteId Identificador del cliente.
     * @return Lista de ventas correspondientes al cliente.
     */
    @Transactional(readOnly = true)
    public List<Venta> findByClienteId(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    /**
     * Guarda o actualiza una venta. Si la fecha de venta no fue proporcionada,
     * se asigna la fecha y hora actuales del sistema.
     *
     * @param venta Venta a persistir.
     * @return Venta persistida con sus cálculos y folios aplicados.
     */
    @Transactional
    public Venta save(Venta venta) {
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }
        return ventaRepository.save(venta);
    }

    /**
     * Elimina una venta por su ID.
     *
     * @param id Identificador de la venta a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    /**
     * Comprueba si una venta existe según su ID.
     *
     * @param id Identificador a verificar.
     * @return {@code true} si existe, {@code false} de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return ventaRepository.existsById(id);
    }
}
