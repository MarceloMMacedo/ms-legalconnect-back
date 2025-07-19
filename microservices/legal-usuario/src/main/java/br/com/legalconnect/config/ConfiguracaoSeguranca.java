package main.java.br.com.legalconnect.config;

// Importações para JWT Filter, AuthenticationEntryPoint e AccessDeniedHandler
// import br.com.legalconnect.usuario.seguranca.JwtAuthenticationFilter;
// import br.com.legalconnect.usuario.seguranca.CustomAuthenticationEntryPoint;
// import br.com.legalconnect.usuario.seguranca.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.legalconnect.usuario.filtro.InterceptorRequisicaoTenant;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Habilita segurança baseada em anotações como @PreAuthorize
public class ConfiguracaoSeguranca extends WebSecurityConfigurerAdapter {

    private final InterceptorRequisicaoTenant interceptorRequisicaoTenant;
    // Adicionar injeção para o filtro JWT, entry point e access denied handler
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    // private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public ConfiguracaoSeguranca(InterceptorRequisicaoTenant interceptorRequisicaoTenant
    /*
     * , JwtAuthenticationFilter jwtAuthenticationFilter,
     * CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
     * CustomAccessDeniedHandler customAccessDeniedHandler
     */) {
        this.interceptorRequisicaoTenant = interceptorRequisicaoTenant;
        // this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        // this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        // this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Desabilita CSRF para APIs REST sem estado
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Configura sessões como
                                                                                            // stateless
                .and()
                .exceptionHandling()
                // Configura handlers para autenticação e autorização
                // .authenticationEntryPoint(customAuthenticationEntryPoint) // Para 401
                // Unauthorized
                // .accessDeniedHandler(customAccessDeniedHandler) // Para 403 Forbidden
                .and()
                .authorizeRequests()
                // Rotas abertas para cadastro de cliente e profissional
                .antMatchers("/api/v1/clientes/cadastro").permitAll()
                .antMatchers("/api/v1/profissionais/cadastro").permitAll()
                // Rotas de redefinição de senha abertas
                .antMatchers("/api/v1/usuarios/redefinir-senha/solicitar").permitAll()
                .antMatchers("/api/v1/usuarios/redefinir-senha/confirmar").permitAll()
                // Rotas de listagem/busca de profissionais abertas para o marketplace
                .antMatchers("/api/v1/profissionais").permitAll()
                .antMatchers("/api/v1/profissionais/{id}").permitAll()
                // Rotas de administrador da plataforma restritas
                .antMatchers("/api/v1/administradores-plataforma/**").hasRole("ADMINISTRADOR_PLATAFORMA")
                // As rotas do Swagger/OpenAPI são configuradas na common-lib.
                // Não é necessário duplicar aqui se a common-lib já as expõe.
                // .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                // "/webjars/**").permitAll()
                // Demais rotas exigem autenticação
                .anyRequest().authenticated();

        // Adiciona o filtro de requisição de tenant antes do filtro de autenticação de
        // usuário e senha
        // Isso garante que o tenantId e userId estejam disponíveis no ContextoTenant
        // antes da autenticação
        http.addFilterBefore(interceptorRequisicaoTenant, UsernamePasswordAuthenticationFilter.class);
        // Adiciona o filtro JWT para autenticação (descomente quando tiver a classe
        // JwtAuthenticationFilter)
        // http.addFilterBefore(jwtAuthenticationFilter,
        // UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Você precisará de um SecurityUtil para as expressões @PreAuthorize
    // Exemplo:
    /*
     * @Bean
     * public SecurityUtil segurancaUtil() {
     * return new SecurityUtil();
     * }
     */
}
