package com.umoar.puntoventa.controller;

import com.umoar.puntoventa.model.Comercio;
import com.umoar.puntoventa.service.ComercioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comercios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ComercioController {

    private final ComercioService comercioService;

    @GetMapping
    public ResponseEntity<List<Comercio>> listar() {
        return ResponseEntity.ok(comercioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comercio> obtenerPorId(@PathVariable Long id) {
        return comercioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comercio> crear(@RequestBody Comercio comercio) {
        Comercio nuevo = comercioService.save(comercio);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comercio> actualizar(@PathVariable Long id, @RequestBody Comercio comercio) {
        return comercioService.findById(id)
                .map(existente -> {
                    comercio.setId(id);
                    return ResponseEntity.ok(comercioService.save(comercio));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!comercioService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        comercioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
