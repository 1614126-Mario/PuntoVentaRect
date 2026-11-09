package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Comercio;
import com.umoar.puntoventa.service.ComercioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Comercios y Sucursales emisoras.
 */
@RestController
@RequestMapping("/api/comercios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Comercios", description = "Gestión de datos de la empresa, comercio y sucursales (Requiere rol ADMIN)")
public class ComercioController {

    private final ComercioService comercioService;

    @GetMapping
    @Operation(summary = "Listar comercios", description = "Retorna el listado de todos los comercios o sucursales configurados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Comercio.class)))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<List<Comercio>> listar() {
        return ResponseEntity.ok(comercioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener comercio por ID", description = "Busca un comercio por su ID único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comercio encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comercio.class))),
        @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    public ResponseEntity<Comercio> obtenerPorId(
            @Parameter(description = "Identificador único del comercio", example = "1", required = true)
            @PathVariable Long id) {
        return comercioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear comercio", description = "Registra un nuevo comercio con su información fiscal.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comercio registrado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comercio.class))),
        @ApiResponse(responseCode = "400", description = "Petición inválida")
    })
    public ResponseEntity<Comercio> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del comercio a registrar", required = true)
            @RequestBody Comercio comercio) {
        Comercio nuevo = comercioService.save(comercio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar comercio", description = "Actualiza los datos de un comercio existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comercio actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comercio.class))),
        @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    public ResponseEntity<Comercio> actualizar(
            @Parameter(description = "Identificador del comercio a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del comercio", required = true)
            @RequestBody Comercio comercio) {
        return comercioService.findById(id)
                .map(existente -> {
                    comercio.setId(id);
                    return ResponseEntity.ok(comercioService.save(comercio));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar comercio", description = "Elimina un comercio por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comercio eliminado"),
        @ApiResponse(responseCode = "404", description = "Comercio no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del comercio a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (!comercioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comercioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
