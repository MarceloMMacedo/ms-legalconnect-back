package br.com.legalconnect.auth.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @class JwtAuthFilter
 * @brief Filtro de requisições para validação de tokens JWT.
 *        Intercepta as requisições, extrai e valida o token JWT do cabeçalho de
 *        autorização.
 *        Adiciona userId e tenantId ao MDC para logging contextual.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Implementa a lógica de filtro para cada requisição.
     * Valida o token JWT presente no cabeçalho Authorization.
     * 
     * @param request     Requisição HTTP.
     * @param response    Resposta HTTP.
     * @param filterChain Cadeia de filtros.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        try {
            // Verifica se o cabeçalho Authorization está presente e começa com "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("Nenhum token JWT encontrado ou formato inválido no cabeçalho Authorization.");
                filterChain.doFilter(request, response);
                return;
            }

            // Extrai o token JWT
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt); // Extrai o e-mail do token

            // Se o e-mail do usuário for válido e não houver autenticação no contexto de
            // segurança
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Tentando autenticar usuário: {}", userEmail);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    // Define o usuário autenticado no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Adiciona userId e tenantId ao MDC
                    MDC.put("userId", String.valueOf(jwtService.extractUserId(jwt)));
                    MDC.put("tenantId", String.valueOf(jwtService.extractTenantId(jwt)));
                    log.info("Usuário '{}' autenticado com sucesso. userId: {}, tenantId: {}", userEmail,
                            MDC.get("userId"), MDC.get("tenantId"));
                } else {
                    log.warn("Token JWT inválido ou expirado para o usuário: {}", userEmail);
                }
            }
        } finally {
            // Garante que o MDC seja limpo após a requisição, mesmo em caso de erro
            filterChain.doFilter(request, response);
            MDC.remove("userId");
            MDC.remove("tenantId");
        }
    }
}