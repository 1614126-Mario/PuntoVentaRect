package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Venta;
import com.umoar.puntoventa.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;

    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByFechaVentaDesc();
    }

    public List<Venta> findByTipoPedido(String tipoPedido) {
        return ventaRepository.findByTipoPedidoOrderByFechaVentaDesc(tipoPedido);
    }

    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> findByClienteId(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    public Venta save(Venta venta) {
        if (venta.getFechaVenta() == null) {
            venta.setFechaVenta(LocalDateTime.now());
        }
        return ventaRepository.save(venta);
    }

    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return ventaRepository.existsById(id);
    }
}
