package com.umoar.puntoventa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad representativa de un cliente del punto de venta o pedidos en línea.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información del cliente para facturación y envíos")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del cliente (autogenerado)", example = "1")
    private Long id;

    @Schema(description = "Nombre(s) del cliente", example = "Juan Carlos", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Apellido(s) del cliente", example = "Pérez Gómez")
    private String apellido;

    @Schema(description = "Documento de identificación tributaria o personal (DUI, NIT, DNI, etc.)", example = "01234567-8")
    private String documento;

    @Schema(description = "Número telefónico de contacto", example = "78901234")
    private String telefono;

    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Dirección de residencia o entrega", example = "Calle Principal #123, San Salvador")
    private String direccion;
}
