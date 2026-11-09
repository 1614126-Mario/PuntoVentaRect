package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Producto;
import com.umoar.puntoventa.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de productos e inventario.
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Retorna todos los productos registrados en el inventario.
     *
     * @return Lista de productos.
     */
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /**
     * Busca un producto por su clave primaria.
     *
     * @param id Identificador único del producto.
     * @return {@link Optional} con el producto correspondiente.
     */
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Busca un producto por su código de barras o SKU.
     *
     * @param codigoBarras Código de barras del producto.
     * @return {@link Optional} con el producto encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Producto> findByCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Guarda o actualiza un producto en el catálogo.
     *
     * @param producto Entidad del producto a persistir.
     * @return Producto persistido.
     */
    @Transactional
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id Identificador del producto a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Verifica la existencia de un producto por su ID.
     *
     * @param id Identificador a verificar.
     * @return {@code true} si existe, {@code false} de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return productoRepository.existsById(id);
    }
}
