package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de Autenticación y Gestión de Sesión de Usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de login, consulta de usuario autenticado y gestión de sesión web")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica al usuario en el sistema con sus credenciales y crea una sesión HTTP con cookie JSESSIONID."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Inicio de sesión exitoso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\": \"SUCCESS\", \"message\": \"Login exitoso\", \"username\": \"admin\", \"roles\": [\"ROLE_ADMIN\"]}")
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales incorrectas o usuario no autorizado",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\": \"ERROR\", \"message\": \"Credenciales incorrectas: usuario o contraseña inválidos\"}")
            )
        )
    })
    public ResponseEntity<Map<String, Object>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciales de acceso", required = true)
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContext securityContext = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.getContextHolderStrategy().setContext(securityContext);

            // Guardar contexto en sesión para persistencia REST
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            securityContextRepository.saveContext(securityContext, request, response);

            responseMap.put("status", "SUCCESS");
            responseMap.put("message", "Login exitoso");
            responseMap.put("username", authentication.getName());
            responseMap.put("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
            return ResponseEntity.ok(responseMap);

        } catch (BadCredentialsException e) {
            responseMap.put("status", "ERROR");
            responseMap.put("message", "Credenciales incorrectas: usuario o contraseña inválidos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        } catch (Exception e) {
            responseMap.put("status", "ERROR");
            responseMap.put("message", "Error durante la autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }
    }

    @GetMapping("/me")
    @Operation(
        summary = "Obtener usuario autenticado",
        description = "Devuelve el nombre de usuario y los roles del usuario actualmente en sesión."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Usuario en sesión devuelto exitosamente",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\": \"SUCCESS\", \"username\": \"admin\", \"roles\": [\"ROLE_ADMIN\"]}")
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No hay usuario autenticado en la sesión",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = "{\"status\": \"ERROR\", \"message\": \"No hay usuario autenticado\"}")
            )
        )
    })
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            response.put("status", "ERROR");
            response.put("message", "No hay usuario autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("status", "SUCCESS");
        response.put("username", authentication.getName());
        response.put("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(response);
    }
}
