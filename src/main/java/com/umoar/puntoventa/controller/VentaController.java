package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.service.ClienteService;
import com.umoar.puntoventa.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el procesamiento, consulta y cobro de Ventas y Comandas.
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','CAJERO','MESERO')")
@Tag(name = "Ventas", description = "Operaciones de registro, consulta por cliente o tipo, y cobro de ventas en el Punto de Venta")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todas las ventas", description = "Obtiene el historial completo de ventas ordenadas de la más reciente a la más antigua.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial de ventas obtenido",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Venta.class)))),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<Venta>> listar() {
        return ResponseEntity.ok(ventaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Devuelve el registro detallado de una venta incluyendo sus renglones y cliente asociado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<Venta> obtenerPorId(
            @Parameter(description = "Identificador único de la venta", example = "100", required = true)
            @PathVariable Long id) {
        return ventaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar ventas por cliente", description = "Filtra el historial de ventas vinculadas a un cliente específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventas del cliente",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Venta.class))))
    })
    public ResponseEntity<List<Venta>> listarPorCliente(
            @Parameter(description = "Identificador del cliente", example = "1", required = true)
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(ventaService.findByClienteId(clienteId));
    }

    @GetMapping("/tipo/{tipoPedido}")
    @Operation(summary = "Listar ventas por modalidad/tipo", description = "Filtra ventas según su tipo de pedido (ej: LOCAL, EN_LINEA, LLEVAR).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ventas filtradas por tipo",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Venta.class))))
    })
    public ResponseEntity<List<Venta>> listarPorTipo(
            @Parameter(description = "Tipo de pedido", example = "LOCAL", schema = @Schema(allowableValues = {"LOCAL", "EN_LINEA", "LLEVAR"}), required = true)
            @PathVariable String tipoPedido) {
        return ResponseEntity.ok(ventaService.findByTipoPedido(tipoPedido));
    }

    @PostMapping
    @Operation(
        summary = "Registrar nueva venta",
        description = "Registra una venta o comanda en el sistema, calculando automáticamente subtotal, IVA del 13%, total y asignando folio correlativo si no se especifica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Venta registrada con éxito",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "400", description = "Datos de venta o detalles inválidos")
    })
    public ResponseEntity<Venta> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Estructura de la venta con sus líneas de detalle", required = true)
            @RequestBody Venta venta) {
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

    @PatchMapping("/{id}/cobro")
    @PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
    @Operation(
        summary = "Cobrar y facturar venta",
        description = "Actualiza el estado de la venta a FACTURADO y asigna opcionalmente el método de pago utilizado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta cobrada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venta.class))),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    public ResponseEntity<Venta> cobrar(
            @Parameter(description = "Identificador de la venta a cobrar", example = "100", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos opcionales del cobro (ej: metodoPago)",
                content = @Content(examples = @ExampleObject(value = "{\"metodoPago\": \"Tarjeta\"}"))
            )
            @RequestBody(required = false) Map<String, String> body) {
        return ventaService.findById(id)
                .map(venta -> {
                    venta.setEstado("FACTURADO");
                    if (body != null && body.get("metodoPago") != null && !body.get("metodoPago").isBlank()) {
                        venta.setMetodoPago(body.get("metodoPago"));
                    }
                    return ResponseEntity.ok(ventaService.save(venta));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar venta", description = "Elimina permanentemente una venta y sus líneas de detalle (Requiere rol ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Venta eliminada"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
        @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador de la venta a eliminar", example = "100", required = true)
            @PathVariable Long id) {
        if (!ventaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
