package br.com.legalconnect.auth.security;

import java.io.IOException;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Importar esta classe
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.multitenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class JwtAuthFilter
 * @brief Filtro de autenticação JWT para requisições HTTP.
 *
 *        Este filtro intercepta todas as requisições, extrai o JWT do cabeçalho
 *        de Autorização,
 *        valida-o e, se válido, configura o contexto de segurança do Spring
 *        Security.
 *        Em caso de token inválido ou expirado, ele delega a resposta ao
 *        `JwtAuthEntryPoint`.
 *        Também define o `tenantId` no `TenantContext` para a thread atual.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class); // Instância do Logger

    @Autowired
    private JwtUtil jwtUtil; /// < Utilitário para manipulação de JWTs.
    @Autowired
    private UserDetailsService userDetailsService; /// < Serviço para carregar detalhes do usuário.

    /**
     * @brief Implementa a lógica do filtro para cada requisição.
     *
     *        Extrai o token JWT do cabeçalho "Authorization", valida-o e, se
     *        válido,
     *        autentica o usuário no contexto de segurança. Em caso de falha, loga o
     *        erro
     *        e permite que o `AuthenticationEntryPoint` lide com a resposta HTTP.
     *
     * @param request     A requisição HTTP.
     * @param response    A resposta HTTP.
     * @param filterChain A cadeia de filtros.
     * @throws ServletException Se ocorrer um erro de servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        log.debug("Iniciando doFilterInternal para a requisição: {}", request.getRequestURI());

        // 1. Verifica se o cabeçalho de Autorização está presente e no formato "Bearer
        // <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Cabeçalho de Autorização ausente ou em formato inválido. Prosseguindo sem autenticação JWT.");
            filterChain.doFilter(request, response); // Continua a cadeia de filtros (requisição não autenticada)
            return;
        }

        jwt = authHeader.substring(7); // Extrai o token JWT (remove "Bearer ")
        log.debug("JWT extraído: {}", jwt);

        try {
            userEmail = jwtUtil.extractUsername(jwt); // Extrai o e-mail do usuário do token
            log.debug("E-mail do usuário extraído do JWT: {}", userEmail);

            // 2. Se o e-mail do usuário foi extraído e não há autenticação no contexto de
            // segurança
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Carregando detalhes do usuário para: {}", userEmail);
                // Carrega os detalhes do usuário usando o UserDetailsService
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("Detalhes do usuário carregados para: {}", userDetails.getUsername());

                // 3. Valida o token e, se válido, autentica o usuário
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    log.debug("Token JWT válido para o usuário: {}", userDetails.getUsername());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // Principal (UserDetails)
                            null, // Credenciais (já validadas pelo JWT, então nulas)
                            userDetails.getAuthorities() // Autoridades/roles do usuário
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request) // Adiciona detalhes da
                                                                                       // requisição
                    );
                    // Define a autenticação no contexto de segurança do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Usuário '{}' autenticado com sucesso no contexto de segurança.",
                            userDetails.getUsername());

                    // 4. Se o userDetails for CustomUserDetails, define o tenantId no TenantContext
                    if (userDetails instanceof CustomUserDetails) {
                        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
                        if (customUserDetails.getTenantId() != null) {
                            TenantContext.setCurrentTenant(customUserDetails.getTenantId().toString());
                            log.debug("TenantContext definido para o tenantId: {}", customUserDetails.getTenantId());
                        } else {
                            log.warn(
                                    "CustomUserDetails para o usuário {} não possui tenantId. TenantContext não será definido.",
                                    userDetails.getUsername());
                        }
                    }
                } else {
                    log.warn("Token JWT inválido para o usuário: {}. Token não corresponde ou expirou.", userEmail);
                }
            } else if (userEmail == null) {
                log.warn("E-mail do usuário não pôde ser extraído do JWT.");
            } else {
                log.debug("Usuário '{}' já autenticado no contexto de segurança. Pulando autenticação JWT.", userEmail);
            }
        } catch (AuthenticationException e) {
            // Esta exceção é lançada intencionalmente para ser capturada pelo
            // JwtAuthEntryPoint
            log.error("Falha na autenticação JWT: {}", e.getMessage());
            throw e; // Re-lança para o EntryPoint
        } catch (Exception e) {
            log.error("Erro inesperado durante a validação do JWT: {}", e.getMessage(), e);
            throw new AuthenticationException("Erro interno durante a autenticação.") {
            };
        }

        // 5. Continua a cadeia de filtros
        filterChain.doFilter(request, response);
        log.debug("Finalizando doFilterInternal para a requisição: {}", request.getRequestURI());
    }
}