package com.umoar.puntoventa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad representativa de una categoría de productos en el catálogo.
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Categoría a la que pertenecen los productos del inventario")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la categoría (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Bebidas", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Descripción detallada de la categoría", example = "Bebidas frías, calientes y refrescos embotellados")
    private String descripcion;
}
