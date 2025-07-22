package br.com.legalconnect.auth.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.auth.service.JwtService;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
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
 *
 *        REMOVIDO: @Component - Este filtro agora é um bean definido na
 *        SecurityConfig para evitar circularidade.
 */
// @Component // REMOVIDO: Este filtro agora é um bean definido na
// SecurityConfig
@RequiredArgsConstructor // Manter RequiredArgsConstructor para injeção via construtor
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

        // Bloco try-finally para garantir que o MDC seja limpo
        try {
            log.debug("Iniciando doFilterInternal para a requisição: {}", request.getRequestURI());

            // 1. Verifica se o cabeçalho de Autorização está presente e no formato "Bearer
            // <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug(
                        "Cabeçalho de Autorização ausente ou em formato inválido. Prosseguindo sem autenticação JWT.");
                filterChain.doFilter(request, response); // Continua a cadeia de filtros (requisição não autenticada)
                return; // Importante: Retorna para não executar o resto do método
            }

            jwt = authHeader.substring(7); // Extrai o token JWT (remove "Bearer ")
            log.debug("JWT extraído: {}", jwt);

            // Tenta extrair o e-mail do usuário do token
            try {
                userEmail = jwtService.extractUsername(jwt);
            } catch (AuthenticationException e) {
                // Captura exceções de autenticação do JWT (assinatura inválida, expirado, etc.)
                log.error("Falha na extração do username ou validação inicial do JWT: {}", e.getMessage());
                // Permite que o JwtAuthEntryPoint lide com a resposta
                throw e; // Re-lança para o AuthenticationEntryPoint
            } catch (Exception e) {
                // Captura outras exceções inesperadas durante a extração do username
                log.error("Erro inesperado ao extrair username do JWT: {}", e.getMessage(), e);
                throw new AuthenticationException("Erro interno durante a autenticação.") {
                }; // Re-lança para o AuthenticationEntryPoint
            }

            // 2. Se o e-mail do usuário foi extraído e não há autenticação no contexto de
            // segurança
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Carregando detalhes do usuário para: {}", userEmail);
                // Carrega os detalhes do usuário usando o UserDetailsService
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("Detalhes do usuário carregados para: {}", userDetails.getUsername());

                // 3. Valida o token e, se válido, autentica o usuário
                if (jwtService.isTokenValid(jwt, userDetails)) {
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

                    // Adiciona userId e tenantId ao MDC
                    // Nota: O userId e tenantId são extraídos do JWT e adicionados ao MDC aqui.
                    // Se o UserDetails for um CustomUserDetails, você pode extrair de lá também.
                    // Para evitar duplicidade de extração, mantemos a lógica no JwtService.
                    MDC.put("userId", String.valueOf(jwtService.extractUserId(jwt)));
                    MDC.put("tenantId", String.valueOf(jwtService.extractTenantId(jwt)));
                    log.debug("MDC definido para userId: {}, tenantId: {}", MDC.get("userId"), MDC.get("tenantId"));

                } else {
                    log.warn("Token JWT inválido ou expirado para o usuário: {}. Token não corresponde ou expirou.",
                            userEmail);
                    throw new BusinessException(ErrorCode.USER_NAO_ENCONTRADO, "Token JWT inválido ou expirado.") {
                    }; // Re-lança para o AuthenticationEntryPoint
                }
            } else if (userEmail == null) {
                log.warn("E-mail do usuário não pôde ser extraído do JWT.");
                throw new BusinessException(ErrorCode.USER_NAO_ENCONTRADO,
                        "Token JWT inválido: E-mail do usuário ausente.") {
                }; // Re-lança para o AuthenticationEntryPoint
            } else {
                log.debug("Usuário '{}' já autenticado no contexto de segurança. Pulando autenticação JWT.", userEmail);
            }

            // 4. Continua a cadeia de filtros APENAS SE A AUTENTICAÇÃO FOI PROCESSADA OU
            filterChain.doFilter(request, response);

        } catch (AuthenticationException e) {
            // Captura exceções de autenticação e as relança para o EntryPoint
            log.error("Falha na autenticação JWT: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado durante o processamento do JWT: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.USER_NAO_ENCONTRADO,
                    "Erro inesperado durante o processamento do JWT") {
            };
        } finally {
            MDC.remove("userId");
            MDC.remove("tenantId");
            log.debug("Finalizando doFilterInternal para a requisição: {}. MDC limpo.", request.getRequestURI());
        }
    }
}
