package com.umoar.puntoventa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración global de OpenAPI para la documentación con Scalar.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Sistema Punto de Venta")
                        .version("1.0.0")
                        .description("Documentación interactiva de la API REST del Sistema Punto de Venta utilizando Scalar. " +
                                "Provee endpoints para autenticación, gestión de catálogo de productos, clientes, comercios, " +
                                "registro de ventas y pedidos públicos en línea.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo PuntoVenta")
                                .email("soporte@puntoventa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("CookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("CookieAuth",
                                new SecurityScheme()
                                        .name("JSESSIONID")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .description("Autenticación basada en sesión web mediante Cookie JSESSIONID tras iniciar sesión en /api/auth/login")));
    }
}
