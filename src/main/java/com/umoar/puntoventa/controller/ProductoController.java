package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Producto;
import com.umoar.puntoventa.service.ProductoService;
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
 * Controlador REST para el catálogo e inventario de Productos.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','CAJERO')")
@Tag(name = "Productos", description = "Gestión del catálogo de productos e inventario (Requiere roles ADMIN o CAJERO)")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Retorna el catálogo completo de productos con su stock y categoría.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de productos",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Producto.class)))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Rol insuficiente")
    })
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca un producto por su clave primaria.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> obtenerPorId(
            @Parameter(description = "Identificador único del producto", example = "1", required = true)
            @PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigoBarras}")
    @Operation(summary = "Buscar producto por código de barras", description = "Permite a lectores de código de barras o búsqueda rápida encontrar un producto por su SKU.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado con ese código")
    })
    public ResponseEntity<Producto> obtenerPorCodigoBarras(
            @Parameter(description = "Código de barras único del producto", example = "7501234567890", required = true)
            @PathVariable String codigoBarras) {
        return productoService.findByCodigoBarras(codigoBarras)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto", description = "Agrega un nuevo producto al catálogo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos")
    })
    public ResponseEntity<Producto> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto a registrar", required = true)
            @RequestBody Producto producto) {
        Producto nuevo = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Modifica los atributos, precio o inventario de un producto.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "Identificador del producto a modificar", example = "1", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos modificados del producto", required = true)
            @RequestBody Producto producto) {
        return productoService.findById(id)
                .map(existente -> {
                    producto.setId(id);
                    return ResponseEntity.ok(productoService.save(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto", description = "Elimina permanentemente un producto del catálogo (Requiere rol ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "403", description = "Requiere rol ADMIN")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del producto a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
