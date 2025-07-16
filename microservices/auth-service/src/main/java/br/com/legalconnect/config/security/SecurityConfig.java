package br.com.legalconnect.config.security;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.legalconnect.auth.auth_service.auth.security.JwtAuthEntryPoint;
import br.com.legalconnect.auth.auth_service.auth.security.JwtAuthFilter;

/**
 * @class SecurityConfig
 * @brief Configuração de segurança principal para o microsserviço de
 *        autenticação.
 *
 *        Esta classe configura o Spring Security para usar JWTs para
 *        autenticação,
 *        define as regras de autorização para endpoints públicos e protegidos,
 *        e integra o filtro JWT personalizado.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita segurança baseada em anotações como @PreAuthorize
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class); // Instância do Logger

    @Autowired
    private UserDetailsService userDetailsService; // Serviço para carregar detalhes do usuário
    @Autowired
    private JwtAuthFilter jwtAuthFilter; // Filtro JWT personalizado
    @Autowired
    private JwtAuthEntryPoint unauthorizedHandler; // Manipulador para requisições não autorizadas
    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de senhas

    /**
     * @brief Configura o provedor de autenticação.
     *
     *        Define o `UserDetailsService` e o `PasswordEncoder` a serem usados
     *        pelo Spring Security para autenticar usuários.
     *
     * @return Uma instância de `DaoAuthenticationProvider`.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        log.debug("Configurando DaoAuthenticationProvider.");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * @brief Obtém o `AuthenticationManager`.
     *
     *        O `AuthenticationManager` é usado para autenticar o objeto
     *        `Authentication`
     *        em métodos de login.
     *
     * @param authConfig A configuração de autenticação.
     * @return Uma instância de `AuthenticationManager`.
     * @throws Exception se ocorrer um erro ao obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.debug("Obtendo AuthenticationManager.");
        return authConfig.getAuthenticationManager();
    }

    /**
     * @brief Configura a cadeia de filtros de segurança HTTP.
     *
     *        Define as regras de autorização para diferentes endpoints,
     *        configura a política de criação de sessão como `STATELESS` (essencial
     *        para JWT),
     *        e adiciona o filtro JWT personalizado antes do filtro de autenticação
     *        padrão do Spring Security.
     *
     * @param http O objeto `HttpSecurity` para configurar a segurança.
     * @return Uma instância de `SecurityFilterChain`.
     * @throws Exception se ocorrer um erro durante a configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando SecurityFilterChain.");
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (não necessário para APIs RESTful com JWT)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Manipulador
                                                                                                         // de exceções
                                                                                                         // de
                                                                                                         // autenticação
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Política
                                                                                                              // de
                                                                                                              // sessão
                                                                                                              // sem
                                                                                                              // estado
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/public/**", "/actuator/**").permitAll() // Permite acesso público a
                                                                                          // endpoints de autenticação e
                                                                                          // Actuator
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acesso ao H2 Console (apenas para
                                                                       // dev)
                        .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
                );

        // Adiciona o provedor de autenticação personalizado
        http.authenticationProvider(authenticationProvider());

        // Adiciona o filtro JWT antes do filtro de autenticação de usuário e senha do
        // Spring Security
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Para H2 Console funcionar com Spring Security (necessário para frames)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        log.info("SecurityFilterChain configurada com sucesso.");
        return http.build();
    }
}