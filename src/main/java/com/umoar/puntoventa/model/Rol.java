package com.umoar.puntoventa.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad representativa de un rol de usuario dentro del sistema de seguridad.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Rol o perfil de seguridad asignado a los usuarios del sistema")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del rol (autogenerado)", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre o identificador del rol (con prefijo ROLE_)", example = "ROLE_ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre; // Ej: ROLE_ADMIN, ROLE_CAJERO, ROLE_MESERO
}
