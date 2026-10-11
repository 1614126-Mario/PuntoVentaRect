package com.umoar.puntoventa.seeder;

import com.umoar.puntoventa.model.Cliente;
import com.umoar.puntoventa.model.Comercio;
import com.umoar.puntoventa.model.Rol;
import com.umoar.puntoventa.model.Usuario;
import com.umoar.puntoventa.repository.ClienteRepository;
import com.umoar.puntoventa.repository.ComercioRepository;
import com.umoar.puntoventa.repository.RolRepository;
import com.umoar.puntoventa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ComercioRepository comercioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsuarios();
        seedComercio();
        seedClientes();
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
        if (rolRepository.findByNombre("ROLE_MESERO").isEmpty()) {
            rolRepository.save(Rol.builder().nombre("ROLE_MESERO").build());
            log.info("✅ Rol creado: ROLE_MESERO");
        }
    }

    private void seedUsuarios() {
        Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));
        Rol rolCajero = rolRepository.findByNombre("ROLE_CAJERO")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_CAJERO no encontrado"));
        Rol rolMesero = rolRepository.findByNombre("ROLE_MESERO")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_MESERO no encontrado"));

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

        // Usuario mesero / mesero123
        usuarioRepository.findByUsername("mesero").ifPresentOrElse(
            user -> {
                user.setPassword(passwordEncoder.encode("mesero123"));
                user.setActivo(true);
                user.setRoles(Set.of(rolMesero));
                usuarioRepository.save(user);
                log.info("✅ Usuario listo → mesero / mesero123 (ROLE_MESERO)");
            },
            () -> {
                Usuario mesero = Usuario.builder()
                        .username("mesero")
                        .password(passwordEncoder.encode("mesero123"))
                        .activo(true)
                        .roles(Set.of(rolMesero))
                        .build();
                usuarioRepository.save(mesero);
                log.info("✅ Usuario creado → mesero / mesero123 (ROLE_MESERO)");
            }
        );
    }

    private void seedComercio() {
        if (comercioRepository.count() == 0) {
            Comercio comercio = Comercio.builder()
                    .nombre("NOCTURNA BAR & GRILL")
                    .nit("NOC-202610-HG1")
                    .direccion("Av. Insurgentes Sur 1234, Ciudad de México")
                    .telefono("+52 55 5555-8899")
                    .build();
            comercioRepository.save(comercio);
            log.info("✅ Comercio creado por defecto: {}", comercio.getNombre());
        }
    }

    private void seedClientes() {
        if (clienteRepository.count() == 0) {
            List<Cliente> clientes = List.of(
                Cliente.builder()
                        .nombre("Consumidor")
                        .apellido("Final")
                        .documento("XAXX010101000")
                        .telefono("555-000-0000")
                        .email("ventas@nocturnagrill.com")
                        .direccion("Consumo en barra / mostrador")
                        .build(),
                Cliente.builder()
                        .nombre("Mariana")
                        .apellido("Flores Cruz")
                        .documento("FLCM920815HA1")
                        .telefono("555-123-4567")
                        .email("mariana.flores@gmail.com")
                        .direccion("Calle Durango 184, Col. Roma Norte, CDMX")
                        .build(),
                Cliente.builder()
                        .nombre("Roberto")
                        .apellido("Sánchez Vega")
                        .documento("SAVR880421KM4")
                        .telefono("555-987-6543")
                        .email("roberto.sanchez@outlook.com")
                        .direccion("Av. Revolución 450, Col. San Ángel, CDMX")
                        .build(),
                Cliente.builder()
                        .nombre("Alejandro")
                        .apellido("Gómez Ruiz")
                        .documento("GORA950110XP8")
                        .telefono("555-456-7890")
                        .email("alejandro.gomez@empresa.com")
                        .direccion("Paseo de la Reforma 222, Piso 14, Cuauhtémoc, CDMX")
                        .build()
            );
            clienteRepository.saveAll(clientes);
            log.info("✅ Clientes iniciales registrados: {}", clientes.size());
        }
    }
}
