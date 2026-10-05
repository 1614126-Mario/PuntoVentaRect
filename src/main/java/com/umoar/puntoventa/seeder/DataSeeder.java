package com.umoar.puntoventa.seeder;

import com.umoar.puntoventa.model.Rol;
import com.umoar.puntoventa.model.Usuario;
import com.umoar.puntoventa.repository.RolRepository;
import com.umoar.puntoventa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsuarios();
    }

    private void seedRoles() {
        if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            rolRepository.save(Rol.builder().nombre("ROLE_ADMIN").build());
            log.info("✅ Rol creado: ROLE_ADMIN");
        }
        if (rolRepository.findByNombre("ROLE_CAJERO").isEmpty()) {
            rolRepository.save(Rol.builder().nombre("ROLE_CAJERO").build());
            log.info("✅ Rol creado: ROLE_CAJERO");
        }
    }

    private void seedUsuarios() {
        Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));
        Rol rolCajero = rolRepository.findByNombre("ROLE_CAJERO")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_CAJERO no encontrado"));

        // Usuario admin / admin123
        usuarioRepository.findByUsername("admin").ifPresentOrElse(
            user -> {
                user.setPassword(passwordEncoder.encode("admin123"));
                user.setActivo(true);
                user.setRoles(Set.of(rolAdmin));
                usuarioRepository.save(user);
                log.info("✅ Usuario listo → admin / admin123 (ROLE_ADMIN)");
            },
            () -> {
                Usuario admin = Usuario.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .activo(true)
                        .roles(Set.of(rolAdmin))
                        .build();
                usuarioRepository.save(admin);
                log.info("✅ Usuario creado → admin / admin123 (ROLE_ADMIN)");
            }
        );

        // Usuario cajero / cajero123
        usuarioRepository.findByUsername("cajero").ifPresentOrElse(
            user -> {
                user.setPassword(passwordEncoder.encode("cajero123"));
                user.setActivo(true);
                user.setRoles(Set.of(rolCajero));
                usuarioRepository.save(user);
                log.info("✅ Usuario listo → cajero / cajero123 (ROLE_CAJERO)");
            },
            () -> {
                Usuario cajero = Usuario.builder()
                        .username("cajero")
                        .password(passwordEncoder.encode("cajero123"))
                        .activo(true)
                        .roles(Set.of(rolCajero))
                        .build();
                usuarioRepository.save(cajero);
                log.info("✅ Usuario creado → cajero / cajero123 (ROLE_CAJERO)");
            }
        );
    }
}
