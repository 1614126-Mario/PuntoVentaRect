package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.DetalleVenta;
import com.umoar.puntoventa.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para los renglones o partidas de detalle de ventas.
 */
@Service
@RequiredArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;

    /**
     * Retorna todos los detalles de venta registrados.
     *
     * @return Lista de detalles.
     */
    @Transactional(readOnly = true)
    public List<DetalleVenta> findAll() {
        return detalleVentaRepository.findAll();
    }

    /**
     * Busca un detalle de venta por su ID.
     *
     * @param id Identificador del detalle.
     * @return {@link Optional} con el detalle encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<DetalleVenta> findById(Long id) {
        return detalleVentaRepository.findById(id);
    }

    /**
     * Busca los renglones de detalle pertenecientes a una venta específica.
     *
     * @param ventaId Identificador de la venta.
     * @return Lista de detalles asociados a esa venta.
     */
    @Transactional(readOnly = true)
    public List<DetalleVenta> findByVentaId(Long ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    /**
     * Guarda o actualiza un renglón de venta.
     *
     * @param detalle Renglón de detalle a persistir.
     * @return Detalle guardado.
     */
    @Transactional
    public DetalleVenta save(DetalleVenta detalle) {
        return detalleVentaRepository.save(detalle);
    }

    /**
     * Elimina un renglón de detalle por su ID.
     *
     * @param id Identificador del detalle a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        detalleVentaRepository.deleteById(id);
    }
}
