package com.umoar.puntoventa.repository;

import com.umoar.puntoventa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Usuario}.
 * Utilizado primordialmente en la autenticación y gestión de usuarios.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario único.
     *
     * @param username Nombre de usuario.
     * @return {@link Optional} con el usuario y sus roles asociados.
     */
    Optional<Usuario> findByUsername(String username);
}
