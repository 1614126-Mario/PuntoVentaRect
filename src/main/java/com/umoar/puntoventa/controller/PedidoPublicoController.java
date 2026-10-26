package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.repository.VentaRepository;
import com.umoar.puntoventa.service.ClienteService;
import com.umoar.puntoventa.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Endpoints públicos (sin autenticación) para que un consumidor haga pedidos en línea
 * y consulte el estado de su pedido. Ruta permitida en SecurityConfig: /api/publico/**
 */
@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PedidoPublicoController {

    private static final BigDecimal IVA = BigDecimal.valueOf(0.13);
    private static final int MAX_ITEMS = 40;
    private static final int MAX_CANTIDAD = 50;
    private static final BigDecimal MAX_PRECIO = BigDecimal.valueOf(500);
    private static final Set<String> METODOS_PAGO = Set.of(
            "Efectivo", "Tarjeta", "Transferencia", "Pago en Línea");

    private final SecureRandom random = new SecureRandom();

    private final VentaService ventaService;
    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;

    public record ItemPedido(String nombre, Integer cantidad, BigDecimal precio) {}

    public record PedidoRequest(
            String nombre,
            String telefono,
            String email,
            String direccionEntrega,
            String referencia,
            String notas,
            String metodoPago,
            List<ItemPedido> items) {}

    @PostMapping("/pedidos")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest req) {
        String nombre = limpiar(req.nombre(), 100);
        String telefono = limpiar(req.telefono(), 30);
        String email = limpiar(req.email(), 100);
        String direccion = limpiar(req.direccionEntrega(), 180);
        String referencia = limpiar(req.referencia(), 60);
        String notas = limpiar(req.notas(), 400);

        if (nombre.isEmpty()) return error("Indica tu nombre.");
        if (telefono.length() < 8) return error("Indica un teléfono de contacto válido.");
        if (direccion.length() < 10) return error("Indica la dirección completa de entrega.");
        if (req.items() == null || req.items().isEmpty() || req.items().size() > MAX_ITEMS) {
            return error("El pedido debe tener entre 1 y " + MAX_ITEMS + " productos.");
        }

        String metodoPago = req.metodoPago() == null ? "" : req.metodoPago().trim();
        if (!METODOS_PAGO.contains(metodoPago)) return error("Forma de pago no válida.");

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemPedido item : req.items()) {
            String nombreProducto = item == null ? "" : limpiar(item.nombre(), 150);
            if (item == null || nombreProducto.isEmpty()
                    || item.cantidad() == null || item.cantidad() < 1 || item.cantidad() > MAX_CANTIDAD
                    || item.precio() == null
                    || item.precio().signum() <= 0 || item.precio().compareTo(MAX_PRECIO) > 0) {
                return error("Hay un producto con datos no válidos en el pedido.");
            }
            BigDecimal linea = item.precio()
                    .multiply(BigDecimal.valueOf(item.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            detalles.add(DetalleVenta.builder()
                    .nombreProducto(nombreProducto)
                    .cantidad(item.cantidad())
                    .precioUnitario(item.precio().setScale(2, RoundingMode.HALF_UP))
                    .subtotal(linea)
                    .build());
            subtotal = subtotal.add(linea);
        }

        BigDecimal iva = subtotal.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

        // Siempre se crea un cliente nuevo para no modificar datos de clientes existentes.
        Cliente cliente = clienteService.save(Cliente.builder()
                .nombre(nombre)
                .telefono(telefono)
                .email(email.isEmpty() ? null : email)
                .direccion(direccion)
                .build());

        String destino = referencia.isEmpty() ? direccion : direccion + " | Ref: " + referencia;

        Venta venta = Venta.builder()
                .fechaVenta(LocalDateTime.now())
                .numeroFactura(generarFolio())
                .tipoPedido("EN_LINEA")
                .estado("PENDIENTE_PAGO")
                .direccionEntrega(destino)
                .metodoPago(metodoPago)
                .notas(notas.isEmpty() ? null : notas)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .iva(iva)
                .total(total)
                .cliente(cliente)
                .build();
        detalles.forEach(d -> d.setVenta(venta));
        venta.setDetalles(detalles);

        Venta guardada = ventaService.save(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(resumen(guardada, destino));
    }

    @GetMapping("/pedidos/{folio}")
    public ResponseEntity<?> consultarPedido(@PathVariable String folio) {
        return ventaRepository.findFirstByNumeroFactura(folio)
                .filter(v -> "EN_LINEA".equals(v.getTipoPedido()))
                .<ResponseEntity<?>>map(v -> ResponseEntity.ok(resumen(v, null)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No encontramos un pedido con ese folio.")));
    }

    private Map<String, Object> resumen(Venta v, String destino) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("folio", v.getNumeroFactura());
        map.put("estado", v.getEstado());
        map.put("fecha", v.getFechaVenta());
        map.put("metodoPago", v.getMetodoPago());
        map.put("subtotal", v.getSubtotal());
        map.put("iva", v.getIva());
        map.put("total", v.getTotal());
        // La dirección solo se devuelve al crear el pedido, no en la consulta por folio.
        if (destino != null) map.put("direccionEntrega", destino);
        List<Map<String, Object>> items = new ArrayList<>();
        for (DetalleVenta d : v.getDetalles()) {
            Map<String, Object> it = new LinkedHashMap<>();
            it.put("nombre", d.getNombreProducto());
            it.put("cantidad", d.getCantidad());
            it.put("precio", d.getPrecioUnitario());
            items.add(it);
        }
        map.put("items", items);
        return map;
    }

    private String generarFolio() {
        String folio;
        do {
            folio = String.format("WEB-%d-%06d", LocalDateTime.now().getYear(), random.nextInt(1_000_000));
        } while (ventaRepository.existsByNumeroFactura(folio));
        return folio;
    }

    private static String limpiar(String valor, int max) {
        if (valor == null) return "";
        String v = valor.trim();
        return v.length() > max ? v.substring(0, max) : v;
    }

    private static ResponseEntity<Map<String, String>> error(String mensaje) {
        return ResponseEntity.badRequest().body(Map.of("message", mensaje));
    }
}
