package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Comercio;
import com.umoar.puntoventa.repository.ComercioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComercioService {

    private final ComercioRepository comercioRepository;

    public List<Comercio> findAll() {
        return comercioRepository.findAll();
    }

    public Optional<Comercio> findById(Long id) {
        return comercioRepository.findById(id);
    }

    public Comercio save(Comercio comercio) {
        return comercioRepository.save(comercio);
    }

    public void deleteById(Long id) {
        comercioRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return comercioRepository.existsById(id);
    }
}
