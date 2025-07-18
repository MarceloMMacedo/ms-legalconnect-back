package br.com.legalconnect.bffpessoas.config;

import br.com.legalconnect.bffpessoas.filter.JwtTokenFilter;
import br.com.legalconnect.user.repository.UserRepository; // Importa o UserRepository da common-lib ou user-service
import br.com.legalconnect.user.entity.User; // Importa a entidade User da common-lib ou user-service
import br.com.legalconnect.auth.security.CustomUserDetails; // Importa CustomUserDetails do auth-service (se reusado)

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class SecurityConfig
 * @brief Configuração de segurança principal para o microsserviço BFF de Pessoas.
 *
 * Esta classe configura o Spring Security para usar JWTs para autenticação,
 * define as regras de autorização para endpoints públicos e protegidos,
 * e integra o filtro JWT personalizado.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita segurança baseada em anotações como @PreAuthorize
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenFilter jwtTokenFilter;
    private final UserRepository userRepository; // Injetar UserRepository para o UserDetailsService

    // Construtor para injeção de dependências
    public SecurityConfig(JwtTokenFilter jwtTokenFilter, UserRepository userRepository) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.userRepository = userRepository;
    }

    /**
     * @brief Configura o provedor de autenticação.
     *
     * Define o `UserDetailsService` e o `PasswordEncoder` a serem usados
     * pelo Spring Security para autenticar usuários. No BFF, o UserDetailsService
     * é usado principalmente para carregar os detalhes do usuário para o SecurityContext,
     * não para autenticar senhas diretamente.
     * @return Uma instância de `DaoAuthenticationProvider`.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        log.debug("Configurando DaoAuthenticationProvider para BFF.");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * @brief Obtém o `AuthenticationManager`.
     *
     * O `AuthenticationManager` é usado para autenticar o objeto `Authentication`
     * em métodos de login (embora o login seja delegado ao auth-service).
     * @param authConfig A configuração de autenticação.
     * @return Uma instância de `AuthenticationManager`.
     * @throws Exception se ocorrer um erro ao obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        log.debug("Obtendo AuthenticationManager para BFF.");
        return authConfig.getAuthenticationManager();
    }

    /**
     * @brief Define o `UserDetailsService` para carregar detalhes do usuário.
     *
     * No contexto do BFF, este `UserDetailsService` é simplificado. Ele busca
     * o usuário apenas pelo e-mail (subject do JWT) para criar um `UserDetails`
     * para o `SecurityContextHolder`. A senha não é validada aqui.
     * @return Uma implementação de `UserDetailsService`.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(User::new) // Converte a entidade User para CustomUserDetails (ou UserDetails diretamente)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado para e-mail no UserDetailsService do BFF: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }

    /**
     * @brief Define o `PasswordEncoder` para criptografia de senhas.
     *
     * Mesmo que o BFF não crie ou valide senhas diretamente, o Spring Security
     * requer um `PasswordEncoder` configurado.
     * @return Uma instância de `BCryptPasswordEncoder`.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Criando bean BCryptPasswordEncoder para BFF.");
        return new BCryptPasswordEncoder();
    }

    /**
     * @brief Configura a cadeia de filtros de segurança HTTP.
     *
     * Define as regras de autorização para diferentes endpoints,
     * configura a política de criação de sessão como `STATELESS` (essencial para JWT),
     * e adiciona o filtro JWT personalizado antes do filtro de autenticação padrão.
     * @param http O objeto `HttpSecurity` para configurar a segurança.
     * @return Uma instância de `SecurityFilterChain`.
     * @throws Exception se ocorrer um erro durante a configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando SecurityFilterChain para BFF.");
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs RESTful com JWT
                // .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Opcional: configurar um entry point para 401
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sessões stateless
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos do BFF (login, refresh, registro de cliente/profissional)
                        .requestMatchers("/api/bff/v1/pessoas/login").permitAll()
                        .requestMatchers("/api/bff/v1/pessoas/refresh-token").permitAll()
                        .requestMatchers("/api/bff/v1/pessoas/cadastro-cliente").permitAll()
                        .requestMatchers("/api/bff/v1/pessoas/cadastro-profissional").permitAll()
                        // Endpoints para documentação da API (Swagger/OpenAPI)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // Endpoints do Actuator (monitoramento)
                        .requestMatchers("/actuator/**").permitAll()
                        // Todas as outras requisições exigem autenticação
                        .anyRequest().authenticated()
                );

        // Adiciona o provedor de autenticação personalizado
        http.authenticationProvider(authenticationProvider());

        // Adiciona o filtro JWT personalizado antes do filtro de autenticação de usuário e senha do Spring Security
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("SecurityFilterChain do BFF configurada com sucesso.");
        return http.build();
    }
}