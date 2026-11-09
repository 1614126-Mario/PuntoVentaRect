package com.umoar.puntoventa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entidad representativa de un producto o artículo para venta e inventario.
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Producto o artículo disponible para la venta")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto (autogenerado)", example = "10")
    private Long id;

    @Schema(description = "Nombre comercial del producto", example = "Café Americano 12oz", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Código de barras o SKU del producto", example = "7501234567890")
    private String codigoBarras;

    @Schema(description = "Precio unitario de venta", example = "2.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en inventario", example = "100")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @Schema(description = "Categoría a la que pertenece el producto")
    private Categoria categoria;
}
