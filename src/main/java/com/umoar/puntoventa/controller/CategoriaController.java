package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Categoria;
import com.umoar.puntoventa.service.CategoriaService;
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
 * Controlador REST para el catálogo de Categorías de productos.
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Categorías", description = "Gestión del catálogo de categorías de productos (Requiere rol ADMIN)")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas las categorías", description = "Retorna el listado completo de categorías de productos registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Categoria.class)))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    })
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Busca y devuelve una categoría específica según su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Categoria> obtenerPorId(
            @Parameter(description = "Identificador único de la categoría", example = "1", required = true)
            @PathVariable Long id) {
        return categoriaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Categoria> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nueva categoría", required = true)
            @RequestBody Categoria categoria) {
        Categoria nueva = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente mediante su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Categoria> actualizar(
            @Parameter(description = "Identificador de la categoría a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la categoría", required = true)
            @RequestBody Categoria categoria) {
        return categoriaService.findById(id)
                .map(existente -> {
                    categoria.setId(id);
                    return ResponseEntity.ok(categoriaService.save(categoria));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina permanentemente una categoría por su identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador de la categoría a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (!categoriaService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
