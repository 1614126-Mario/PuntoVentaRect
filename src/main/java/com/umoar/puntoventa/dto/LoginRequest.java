package com.umoar.puntoventa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de autenticación de usuarios.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales para la autenticación de usuarios en el sistema")
public class LoginRequest {

    @Schema(description = "Nombre de usuario registrado", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Contraseña del usuario", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
