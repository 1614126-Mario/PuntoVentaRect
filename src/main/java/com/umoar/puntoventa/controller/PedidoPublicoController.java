package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.repository.VentaRepository;
import com.umoar.puntoventa.service.ClienteService;
import com.umoar.puntoventa.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Endpoints públicos (sin autenticación requerida) para realizar pedidos en línea y consultar su estado.
 */
@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
@Tag(name = "Pedidos Públicos", description = "Endpoints de acceso público para que clientes finales hagan pedidos en línea y consulten su estado")
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

    @Schema(description = "Ítem o producto seleccionado dentro de una orden de pedido público")
    public record ItemPedido(
            @Schema(description = "Nombre o descripción del producto", example = "Hamburguesa Clásica", requiredMode = Schema.RequiredMode.REQUIRED)
            String nombre,

            @Schema(description = "Cantidad ordenada", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer cantidad,

            @Schema(description = "Precio unitario acordado", example = "5.50", requiredMode = Schema.RequiredMode.REQUIRED)
            BigDecimal precio
    ) {}

    @Schema(description = "Petición de creación de pedido por parte de un cliente final")
    public record PedidoRequest(
            @Schema(description = "Nombre completo de quien recibe el pedido", example = "María Hernández", requiredMode = Schema.RequiredMode.REQUIRED)
            String nombre,

            @Schema(description = "Teléfono de contacto para el repartidor (mínimo 8 dígitos)", example = "78901234", requiredMode = Schema.RequiredMode.REQUIRED)
            String telefono,

            @Schema(description = "Correo electrónico para confirmación", example = "maria.hernandez@example.com")
            String email,

            @Schema(description = "Dirección completa de entrega a domicilio", example = "Colonia Miramonte, Calle Los Cedros #23", requiredMode = Schema.RequiredMode.REQUIRED)
            String direccionEntrega,

            @Schema(description = "Puntos de referencia para facilitar la llegada", example = "Frente al parque central, portón negro")
            String referencia,

            @Schema(description = "Notas adicionales o instrucciones especiales", example = "Tocar el timbre con fuerza, sin cebolla por favor")
            String notas,

            @Schema(description = "Método de pago seleccionado", example = "Efectivo", allowableValues = {"Efectivo", "Tarjeta", "Transferencia", "Pago en Línea"}, requiredMode = Schema.RequiredMode.REQUIRED)
            String metodoPago,

            @Schema(description = "Listado de productos del pedido", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ItemPedido> items
    ) {}

    @PostMapping("/pedidos")
    @Operation(
        summary = "Crear pedido público en línea",
        description = "Permite a un consumidor final registrar un nuevo pedido sin necesidad de autenticarse, calculando subtotal, IVA y generando un folio único."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Pedido creado satisfactoriamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"folio\": \"WEB-2026-123456\", \"estado\": \"PENDIENTE_PAGO\", \"total\": 12.43, \"items\": [{\"nombre\": \"Hamburguesa\", \"cantidad\": 2, \"precio\": 5.50}]}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos del pedido incompletos o inválidos",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Indica tu nombre.\"}"))
        )
    })
    public ResponseEntity<?> crearPedido(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pedido y del cliente", required = true)
            @RequestBody PedidoRequest req) {
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
    @Operation(
        summary = "Consultar estado de pedido por folio",
        description = "Consulta la información resumida y estado actual de un pedido en línea mediante su número de folio."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Información del pedido encontrada",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"folio\": \"WEB-2026-123456\", \"estado\": \"PENDIENTE_PAGO\", \"total\": 12.43}")
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No se encontró ningún pedido en línea con ese folio",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"No encontramos un pedido con ese folio.\"}"))
        )
    })
    public ResponseEntity<?> consultarPedido(
            @Parameter(description = "Folio del pedido (formato: WEB-YYYY-NNNNNN)", example = "WEB-2026-000123", required = true)
            @PathVariable String folio) {
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
