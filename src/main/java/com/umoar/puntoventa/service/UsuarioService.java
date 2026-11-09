package com.umoar.puntoventa.service;

import com.umoar.puntoventa.model.Usuario;
import com.umoar.puntoventa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la administración de usuarios y proveedor
 * de detalles de usuario para la autenticación en Spring Security ({@link UserDetailsService}).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga el usuario y sus roles asignados para el proceso de autenticación de Spring Security.
     *
     * @param username Nombre de usuario introducido en el login.
     * @return {@link UserDetails} con el nombre de usuario, contraseña encriptada y autoridades (roles).
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true, true, true,
                authorities
        );
    }

    /**
     * Retorna la lista de todos los usuarios registrados.
     *
     * @return Lista de usuarios.
     */
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su clave primaria.
     *
     * @param id Identificador del usuario.
     * @return {@link Optional} con el usuario correspondiente.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return {@link Optional} con el usuario encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Guarda o actualiza un usuario en la base de datos.
     *
     * @param usuario Entidad del usuario a persistir.
     * @return Usuario persistido.
     */
    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id Identificador del usuario a eliminar.
     */
    @Transactional
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    /**
     * Comprueba si existe un usuario con el ID especificado.
     *
     * @param id Identificador a verificar.
     * @return {@code true} si existe, {@code false} de lo contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }
}
