package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.service.ClienteService;
import com.umoar.puntoventa.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Venta>> listar() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        return ventaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.findByClienteId(clienteId));
    }

    @GetMapping("/tipo/{tipoPedido}")
    public ResponseEntity<List<Venta>> listarPorTipo(@PathVariable String tipoPedido) {
        return ResponseEntity.ok(ventaService.findByTipoPedido(tipoPedido));
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody Venta venta) {
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }

        // Manejo o vinculación de cliente si viene especificado
        if (venta.getCliente() != null) {
            if (venta.getCliente().getId() != null) {
                clienteService.findById(venta.getCliente().getId()).ifPresent(venta::setCliente);
            } else if (venta.getCliente().getNombre() != null && !venta.getCliente().getNombre().isBlank()) {
                // Si el cliente no tiene ID pero sí datos, se guarda automáticamente
                Cliente nuevoCliente = clienteService.save(venta.getCliente());
                venta.setCliente(nuevoCliente);
            }
        }

        // Generar número/folio de factura si no viene
        if (venta.getNumeroFactura() == null || venta.getNumeroFactura().isBlank()) {
            long secuencia = System.currentTimeMillis() % 1000000;
            venta.setNumeroFactura(String.format("FAC-%d-%06d", LocalDateTime.now().getYear(), secuencia));
        }

        if (venta.getTipoPedido() == null || venta.getTipoPedido().isBlank()) {
            venta.setTipoPedido("LOCAL");
        }

        if (venta.getEstado() == null || venta.getEstado().isBlank()) {
            venta.setEstado("FACTURADO");
        }

        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getCantidad() != null && detalle.getPrecioUnitario() != null) {
                    BigDecimal subtotal = detalle.getPrecioUnitario()
                            .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                            .setScale(2, RoundingMode.HALF_UP);
                    detalle.setSubtotal(subtotal);
                    detalle.setVenta(venta);
                    subtotalCalculado = subtotalCalculado.add(subtotal);
                }
            }
        }

        if (venta.getSubtotal() == null) {
            venta.setSubtotal(subtotalCalculado.setScale(2, RoundingMode.HALF_UP));
        }

        if (venta.getIva() == null) {
            BigDecimal ivaCalculado = venta.getSubtotal().multiply(BigDecimal.valueOf(0.13)).setScale(2, RoundingMode.HALF_UP);
            venta.setIva(ivaCalculado);
        }

        if (venta.getTotal() == null) {
            venta.setTotal(venta.getSubtotal().add(venta.getIva()).setScale(2, RoundingMode.HALF_UP));
        }

        Venta nueva = ventaService.save(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!ventaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
