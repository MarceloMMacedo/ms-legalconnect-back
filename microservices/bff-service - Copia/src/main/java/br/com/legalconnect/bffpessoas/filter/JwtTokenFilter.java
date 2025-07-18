package br.com.legalconnect.bffpessoas.filter;

import br.com.legalconnect.bffpessoas.util.JwtUtilBFF;
import br.com.legalconnect.common.config.multitenancy.TenantContext; // Importa TenantContext da common-lib
import br.com.legalconnect.user.entity.User; // Importa a entidade User (assumindo que User implementa UserDetails)
import br.com.legalconnect.user.repository.UserRepository; // Importa UserRepository para carregar UserDetails

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC; // Importa MDC para logging contextual
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @class JwtTokenFilter
 * @brief Filtro de requisições para validação de tokens JWT no BFF.
 *
 * Intercepta as requisições, extrai e valida o token JWT do cabeçalho de autorização.
 * Se o token for válido, popula o `SecurityContextHolder` e o `TenantContext`
 * com `userId` e `tenantId` extraídos do token, e adiciona esses IDs ao MDC
 * para logging contextualizado.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtUtilBFF jwtUtilBFF;
    private final UserRepository userRepository; // Usado para carregar UserDetails

    /**
     * @brief Implementa a lógica do filtro para cada requisição.
     *
     * Extrai o token JWT do cabeçalho "Authorization", valida-o e, se válido,
     * configura o contexto de segurança do Spring Security e o TenantContext.
     * Adiciona `userId` e `tenantId` ao MDC.
     * @param request A requisição HTTP.
     * @param response A resposta HTTP.
     * @param filterChain A cadeia de filtros.
     * @throws ServletException Se ocorrer um erro de servlet.
     * @throws java.io.IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, java.io.IOException {

        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;
        Long userId = null;
        Long tenantId = null;

        // Bloco try-finally para garantir que o MDC e TenantContext sejam limpos
        try {
            // 1. Verifica se o cabeçalho de Autorização está presente e no formato "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("Cabeçalho de Autorização ausente ou em formato inválido. Prosseguindo sem autenticação JWT.");
                filterChain.doFilter(request, response); // Continua a cadeia de filtros (requisição não autenticada)
                return;
            }

            jwt = authHeader.substring(7); // Extrai o token JWT (remove "Bearer ")
            log.debug("JWT extraído: {}", jwt);

            // 2. Extrai informações do token usando JwtUtilBFF
            userEmail = jwtUtilBFF.extractUsername(jwt);
            userId = jwtUtilBFF.extractUserId(jwt);
            tenantId = jwtUtilBFF.extractTenantId(jwt);

            log.debug("Informações extraídas do JWT: email={}, userId={}, tenantId={}", userEmail, userId, tenantId);

            // 3. Se o e-mail do usuário foi extraído e não há autenticação no contexto de segurança
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Carregando detalhes do usuário para: {}", userEmail);
                // Carrega os detalhes do usuário usando o UserRepository (User implementa UserDetails)
                UserDetails userDetails = userRepository.findByEmail(userEmail)
                        .map(User::new) // Converte a entidade User para UserDetails
                        .orElseThrow(() -> {
                            log.warn("Usuário não encontrado no banco de dados para e-mail do JWT: {}", userEmail);
                            return new UsernameNotFoundException("Usuário não encontrado com e-mail: " + userEmail);
                        });

                log.debug("Detalhes do usuário carregados para: {}", userDetails.getUsername());

                // 4. Valida o token e, se válido, autentica o usuário
                if (jwtUtilBFF.isTokenValid(jwt, userDetails)) {
                    log.debug("Token JWT válido para o usuário: {}", userDetails.getUsername());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, // Principal (UserDetails)
                            null, // Credenciais (já validadas pelo JWT, então nulas)
                            userDetails.getAuthorities() // Autoridades/roles do usuário
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Define a autenticação no contexto de segurança do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Usuário '{}' autenticado com sucesso no contexto de segurança.", userDetails.getUsername());

                    // 5. Define o tenantId no TenantContext (para FeignClientConfig e lógica de negócio)
                    if (tenantId != null) {
                        TenantContext.setCurrentTenant(tenantId.toString());
                        log.debug("TenantContext definido para o tenantId: {}", tenantId);
                    } else {
                        log.warn("TenantId não encontrado no JWT para o usuário {}. TenantContext não será definido.", userEmail);
                    }

                    // 6. Adiciona userId e tenantId ao MDC para logging contextualizado
                    MDC.put("userId", String.valueOf(userId));
                    MDC.put("tenantId", String.valueOf(tenantId));
                    log.debug("MDC populado com userId: {} e tenantId: {}", userId, tenantId);

                } else {
                    log.warn("Token JWT inválido ou expirado para o usuário: {}. Token não corresponde ou expirou.", userEmail);
                }
            } else if (userEmail == null) {
                log.warn("E-mail do usuário não pôde ser extraído do JWT.");
            } else {
                log.debug("Usuário '{}' já autenticado no contexto de segurança. Pulando autenticação JWT.", userEmail);
            }

            // Continua a cadeia de filtros
            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException e) {
            log.warn("Falha na autenticação JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("{"status":"ERROR","message":"" + e.getMessage() + ""}");
        } catch (Exception e) {
            log.error("Erro inesperado durante a validação do JWT: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{"status":"ERROR","message":"Erro interno durante a autenticação: " + e.getMessage() + ""}");
        } finally {
            // Garante que o MDC e TenantContext sejam limpos após a requisição
            MDC.remove("userId");
            MDC.remove("tenantId");
            TenantContext.clear();
            log.debug("MDC e TenantContext limpos após a requisição.");
        }
    }
}