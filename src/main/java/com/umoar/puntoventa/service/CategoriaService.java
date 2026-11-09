package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Categoria;
import com.umoar.puntoventa.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la gestión de categorías de productos.
 */
@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * Retorna todas las categorías registradas en el catálogo.
     *
     * @return Lista de categorías.
     */
    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    /**
     * Busca una categoría por su identificador primario.
     *
     * @param id Identificador de la categoría.
     * @return {@link Optional} con la categoría si fue encontrada.
     */
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Registra o actualiza una categoría.
     *
     * @param categoria Entidad a persistir.
     * @return Categoría persistida con su ID generado.
     */
    @Transactional
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id Identificador de la categoría a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }

    /**
     * Comprueba la existencia de una categoría por su identificador.
     *
     * @param id Identificador a verificar.
     * @return {@code true} si existe, {@code false} de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return categoriaRepository.existsById(id);
    }
}
