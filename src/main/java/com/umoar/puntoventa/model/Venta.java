package com.umoar.puntoventa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad representativa de una transacción de venta, orden o factura.
 */
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Registro completo de una transacción o pedido de venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la venta (autogenerado)", example = "100")
    private Long id;

    @Column(name = "fecha_venta")
    @Builder.Default
    @Schema(description = "Fecha y hora en la que se generó la venta", example = "2026-11-09 10:15:00")
    private LocalDateTime fechaVenta = LocalDateTime.now();

    @Schema(description = "Monto total a pagar (subtotal + IVA)", example = "11.30")
    private BigDecimal total;

    @Schema(description = "Subtotal neto antes de impuestos", example = "10.00")
    private BigDecimal subtotal;

    @Schema(description = "Monto del impuesto al valor agregado (IVA)", example = "1.30")
    private BigDecimal iva;

    @Column(name = "numero_factura")
    @Schema(description = "Número o folio correlativo de la factura o comprobante", example = "FAC-2026-000123")
    private String numeroFactura;

    @Column(name = "tipo_pedido")
    @Schema(description = "Tipo o modalidad de pedido", example = "LOCAL", allowableValues = {"LOCAL", "EN_LINEA", "LLEVAR"})
    private String tipoPedido; // "LOCAL", "EN_LINEA", "LLEVAR"

    @Column(name = "numero_mesa")
    @Schema(description = "Identificador de la mesa (aplica para consumo local)", example = "Mesa 4")
    private String numeroMesa; // Ej: "Mesa 4"

    @Column(name = "direccion_entrega")
    @Schema(description = "Dirección y referencias para entregas a domicilio", example = "Col. Escalón, Pasaje 2, Casa #15")
    private String direccionEntrega;

    @Column(name = "metodo_pago")
    @Schema(description = "Método o forma de pago", example = "Efectivo", allowableValues = {"Efectivo", "Tarjeta", "Transferencia", "Pago en Línea"})
    private String metodoPago;

    @Schema(description = "Estado actual de la venta u orden", example = "FACTURADO", allowableValues = {"FACTURADO", "PENDIENTE_PAGO", "ANULADO"})
    private String estado;

    @Column(length = 500)
    @Schema(description = "Observaciones o instrucciones adicionales del pedido", example = "Entregar sin azúcar y con servilletas extra")
    private String notas;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @Schema(description = "Cliente asociado a la venta")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta")
    @Builder.Default
    @Schema(description = "Lista de renglones o productos contenidos en la orden")
    private List<DetalleVenta> detalles = new ArrayList<>();
}
