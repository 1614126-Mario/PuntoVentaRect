package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Comercio;
import com.umoar.puntoventa.repository.ComercioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la administración del comercio o sucursal.
 */
@Service
@RequiredArgsConstructor
public class ComercioService {

    private final ComercioRepository comercioRepository;

    /**
     * Obtiene todos los comercios registrados.
     *
     * @return Lista de comercios.
     */
    @Transactional(readOnly = true)
    public List<Comercio> findAll() {
        return comercioRepository.findAll();
    }

    /**
     * Busca un comercio por su ID único.
     *
     * @param id Identificador del comercio.
     * @return {@link Optional} con el comercio encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Comercio> findById(Long id) {
        return comercioRepository.findById(id);
    }

    /**
     * Registra o actualiza los datos de un comercio.
     *
     * @param comercio Entidad del comercio a persistir.
     * @return Comercio persistido.
     */
    @Transactional
    public Comercio save(Comercio comercio) {
        return comercioRepository.save(comercio);
    }

    /**
     * Elimina un comercio por su ID.
     *
     * @param id Identificador del comercio a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        comercioRepository.deleteById(id);
    }

    /**
     * Verifica la existencia de un comercio por su ID.
     *
     * @param id Identificador a comprobar.
     * @return {@code true} si existe, {@code false} en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return comercioRepository.existsById(id);
    }
}
