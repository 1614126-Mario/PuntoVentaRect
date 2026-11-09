package com.umoar.puntoventa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad representativa de la sucursal o comercio emisor de facturación.
 */
@Entity
@Table(name = "comercios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos generales y fiscales del comercio o sucursal")
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del comercio (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "Nombre comercial o razón social", example = "Restaurante y Café Central", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Número de Identificación Tributaria (NIT / CIF)", example = "0614-010190-101-1")
    private String nit;

    @Schema(description = "Dirección física del establecimiento", example = "Av. Las Rosas #45, San Salvador")
    private String direccion;

    @Schema(description = "Teléfono de atención del comercio", example = "22345678")
    private String telefono;
}
