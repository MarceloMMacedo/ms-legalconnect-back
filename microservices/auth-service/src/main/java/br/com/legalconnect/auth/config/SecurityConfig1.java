// auth-service/src/main/java/br/com/legalconnect/config/security/SecurityConfig.java
package br.com.legalconnect.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource; // Importar CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.legalconnect.auth.service.JwtService; // Importar JwtService
import br.com.legalconnect.user.repository.UserRepository; // Importar UserRepository

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
public class SecurityConfig1 {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig1.class);
    // @Autowired
    // private JwtAuthEntryPoint unauthorizedHandler;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService; // Injetar JwtService

    @Bean

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/api/v1/public/**",
            "/api/v1/users/register/**",
            "/api/v1/users/recover-password/**",
            "/api/v1/users/reset-password/**",
            "/actuator/**",
            "/h2-console/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    /**
     * @brief Configura o provedor de autenticação.
     *
     *        Define o `UserDetailsService` e o `PasswordEncoder` a serem usados
     *        pelo Spring Security para autenticar usuários.
     *
     * @param userDetailsService O UserDetailsService (será injetado pelo Spring)
     * @return Uma instância de `DaoAuthenticationProvider`.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        log.debug("Configurando DaoAuthenticationProvider.");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
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
     * @brief Define o UserDetailsService personalizado.
     *
     *        Este bean é criado pelo Spring e injetará o UserRepository.
     * @return Uma instância de UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        log.debug("Criando bean UserDetailsService personalizado.");
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    /**
     * @brief Define o JwtAuthFilter como um bean.
     *
     *        Ao definir o filtro como um bean aqui, o Spring gerencia sua criação
     *        e injeção de dependências, quebrando a referência circular que ocorria
     *        quando ele era um @Component e injetado via @Autowired na
     *        SecurityConfig.
     *
     * @param jwtService         O serviço JWT injetado pelo Spring.
     * @param userDetailsService O serviço de detalhes do usuário injetado pelo
     *                           Spring.
     * @return Uma instância de JwtAuthFilter.
     */
    // @Bean
    // public JwtAuthFilter jwtAuthFilter(JwtService jwtService, UserDetailsService
    // userDetailsService) {
    // log.debug("Criando bean JwtAuthFilter.");
    // return new JwtAuthFilter(jwtService, userDetailsService);
    // }

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
                .csrf(csrf -> csrf.disable())
                // Adicionar configuração CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // .exceptionHandling(exception ->
                // exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider(userDetailsService()));

        // Usa o bean jwtAuthFilter que foi definido acima
        // http.addFilterBefore(jwtAuthFilter(jwtService, userDetailsService()),
        // UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        log.info("SecurityFilterChain configurada com sucesso.");
        return http.build();
    }
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // log.info("Configurando SecurityFilterChain.");
    // http
    // .csrf(csrf -> csrf.disable())
    // .exceptionHandling(exception ->
    // exception.authenticationEntryPoint(unauthorizedHandler))
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
    // .anyRequest().authenticated());

    // http.authenticationProvider(authenticationProvider(userDetailsService()));

    // // Usa o bean jwtAuthFilter que foi definido acima
    // http.addFilterBefore(jwtAuthFilter(jwtService, userDetailsService()),
    // UsernamePasswordAuthenticationFilter.class);

    // http.headers(headers -> headers.frameOptions(frameOptions ->
    // frameOptions.sameOrigin()));

    // log.info("SecurityFilterChain configurada com sucesso.");
    // return http.build();
    // }
    /**
     * @brief Configura o CorsConfigurationSource para permitir requisições CORS.
     *
     *        Define as origens permitidas, métodos HTTP, cabeçalhos e credenciais
     *        para as requisições CORS.
     * @return Uma instância de `CorsConfigurationSource`.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.debug("Configurando CorsConfigurationSource.");
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir todas as origens (em produção, especifique origens seguras, ex:
        // "http://localhost:3000")
        configuration.addAllowedOrigin("*");
        // Permitir todos os métodos HTTP (GET, POST, PUT, DELETE, etc.)
        configuration.addAllowedMethod("*");
        // Permitir todos os cabeçalhos
        configuration.addAllowedHeader("*");
        // Permitir envio de credenciais (cookies, headers de autorização)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuração CORS a todos os caminhos (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
