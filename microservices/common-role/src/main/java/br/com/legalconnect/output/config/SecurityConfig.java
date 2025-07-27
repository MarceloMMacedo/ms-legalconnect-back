package br.com.legalconnect.output.config; // Pacote da lib

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.legalconnect.output.security.UserRolesHeaderFilter;

/**
 * Configuração principal do Spring Security para integrar o filtro de roles.
 * Esta classe é parte da biblioteca common-role e deve ser importada ou
 * escaneada
 * pela aplicação consumidora.
 */
@Configuration
@EnableWebSecurity // Habilita a integração de segurança da web do Spring Security
public class SecurityConfig {

    private final UserRolesHeaderFilter userRolesHeaderFilter;

    public SecurityConfig(UserRolesHeaderFilter userRolesHeaderFilter) {
        this.userRolesHeaderFilter = userRolesHeaderFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll() // Permite tudo por padrão e usa @PreAuthorize
                )
                .addFilterBefore(userRolesHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}