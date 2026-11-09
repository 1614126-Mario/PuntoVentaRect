package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Rol}.
 * Acceso y consulta de los roles de usuario del sistema.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Busca un rol por su nombre único (ej: ROLE_ADMIN, ROLE_CAJERO).
     *
     * @param nombre Nombre del rol con el prefijo ROLE_.
     * @return {@link Optional} con el rol encontrado.
     */
    Optional<Rol> findByNombre(String nombre);
}
