package br.com.legalconnect.output.config; // Pacote da lib

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuração para habilitar a segurança em nível de método na aplicação que
 * consumir a lib.
 * Isso permite o uso de anotações como @PreAuthorize e @PostAuthorize.
 */
@Configuration
@EnableMethodSecurity // Habilita a segurança em nível de método para o Spring Security 6+
public class MethodSecurityConfig {
    // Esta classe não precisa de conteúdo adicional para habilitar a
    // funcionalidade.
}