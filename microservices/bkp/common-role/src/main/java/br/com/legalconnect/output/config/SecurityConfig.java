package br.com.legalconnect.output.config; // Pacote da lib

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/publico/**").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(userRolesHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // http
    // .csrf(csrf -> csrf.disable()) // Desabilita CSRF se for uma API REST
    // stateless
    // .cors(cors -> cors.configurationSource(corsConfigurationSource())) //
    // Habilita e configura CORS
    // // ... outras configurações de autorização e autenticação
    // .authorizeHttpRequests(authorize -> authorize
    // .requestMatchers("/api/v1/publico/**").permitAll() // Permite acesso público
    // a endpoints específicos
    // .anyRequest().authenticated()
    // );
    // return http.build();
    // }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*", "http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica CORS a todos os caminhos
        return source;
    }
}