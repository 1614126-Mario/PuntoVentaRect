package com.umoar.puntoventa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entidad representativa de una línea o renglón de detalle dentro de una venta.
 */
@Entity
@Table(name = "detalle_ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Detalle o renglón de producto incluido en una venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del detalle de venta (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "Cantidad de unidades compradas", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @Schema(description = "Precio unitario aplicado en la venta", example = "2.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precioUnitario;

    @Schema(description = "Subtotal de la línea (cantidad * precioUnitario)", example = "5.00")
    private BigDecimal subtotal;

    @Column(name = "nombre_producto")
    @Schema(description = "Nombre o descripción del producto capturado en el momento de la venta", example = "Café Americano 12oz")
    private String nombreProducto;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnoreProperties("detalles")
    @Schema(description = "Venta a la que pertenece esta línea de detalle")
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    @Schema(description = "Producto del catálogo referenciado (opcional si es venta libre o pedido rápido)")
    private Producto producto;
}
