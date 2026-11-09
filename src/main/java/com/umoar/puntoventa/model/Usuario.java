package com.umoar.puntoventa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad representativa de un usuario operador del sistema de punto de venta.
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Usuario con credenciales y roles para operar en la plataforma")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario (autogenerado)", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre de usuario único para inicio de sesión", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada en BCrypt (solo para escritura / petición)", example = "secreta123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Builder.Default
    @Schema(description = "Estado de actividad del usuario", example = "true")
    private boolean activo = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    @Schema(description = "Conjunto de roles asignados al usuario")
    private Set<Rol> roles = new HashSet<>();
}
