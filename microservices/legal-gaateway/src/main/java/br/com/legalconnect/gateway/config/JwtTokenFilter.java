package br.com.legalconnect.gateway.config;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR;

import java.util.HashMap; // Adicionado para inicializar o mapa de rotas de exemplo
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * JwtTokenFilter é um filtro de gateway para o Spring Cloud Gateway que
 * intercepta
 * as requisições para validar tokens JWT e aplicar regras de segurança.
 * Ele verifica rotas públicas, valida o token, e aplica regras de autorização
 * baseadas em roles e scopes definidos por rota.
 */
@Component
public class JwtTokenFilter implements GatewayFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final SecurityProperties securityProperties;

    /**
     * Construtor para injetar as dependências JwtUtil e SecurityProperties.
     * 
     * @param jwtUtil            Utilitário para manipulação de JWT.
     * @param securityProperties Propriedades de segurança da aplicação.
     */
    public JwtTokenFilter(JwtUtil jwtUtil, SecurityProperties securityProperties) {
        this.jwtUtil = jwtUtil;
        this.securityProperties = securityProperties;
    }

    /**
     * Método principal do filtro que processa a requisição.
     * 
     * @param exchange O contexto da requisição e resposta.
     * @param chain    A cadeia de filtros para continuar o processamento.
     * @return Um Mono<Void> que indica a conclusão do processamento.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 1. Verifica se a rota é pública e, se for, permite o acesso sem validação de
        // token.
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // 2. Extrai o token JWT do cabeçalho da requisição.
        String token = extractToken(request);

        // 3. Valida o token. Se for nulo ou inválido, retorna 401 Unauthorized.
        if (token == null || !jwtUtil.validateToken(token)) {
            return unauthorized(exchange);
        }

        // 4. Verifica configurações específicas da rota (role e scopes).
        // GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR é um atributo do Spring Cloud
        // Gateway
        // que contém o ID da rota que correspondeu à requisição.
        String routeId = (String) exchange.getAttribute(GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
        if (routeId != null && securityProperties.getRoutes().containsKey(routeId)) {
            RouteConfig routeConfig = securityProperties.getRoutes().get(routeId);
            // Valida o token com base nas roles e scopes exigidos pela rota.
            if (!jwtUtil.validateTokenForRoute(token, routeConfig.getRequiredRole(), routeConfig.getRequiredScopes())) {
                return forbidden(exchange); // Retorna 403 Forbidden se a validação falhar.
            }
        }

        // 5. Adiciona cabeçalhos à requisição com informações extraídas do token e
        // continua a cadeia de filtros.
        return chain.filter(exchange.mutate()
                .request(addHeaders(request, token))
                .build());
    }

    /**
     * Verifica se o caminho da requisição corresponde a algum padrão de rota
     * pública.
     * 
     * @param path O caminho da requisição.
     * @return true se o caminho for público, false caso contrário.
     */
    private boolean isPublicPath(String path) {
        // Usa AntPathMatcher para comparar o caminho com os padrões configurados.
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        return securityProperties.getPublicPaths().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    /**
     * Extrai o token JWT do cabeçalho Authorization da requisição.
     * Este é um método placeholder e precisa ser implementado de acordo com a sua
     * lógica
     * de extração de token (e.g., de um cabeçalho "Authorization: Bearer <token>").
     * 
     * @param request A requisição do servidor.
     * @return O token JWT como String, ou null se não for encontrado.
     */
    private String extractToken(ServerHttpRequest request) {
        // Implementação de exemplo: extrai de um cabeçalho "Authorization: Bearer
        // <token>"
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer "
        }
        return null;
    }

    /**
     * Adiciona cabeçalhos à requisição com informações extraídas dos claims do
     * token JWT.
     * 
     * @param request A requisição original.
     * @param token   O token JWT validado.
     * @return A requisição com os novos cabeçalhos.
     */
    private ServerHttpRequest addHeaders(ServerHttpRequest request, String token) {
        Map<String, Object> claims = jwtUtil.getAllClaimsFromToken(token);
        ServerHttpRequest.Builder builder = request.mutate();

        // Adiciona o cabeçalho Authorization novamente (pode ser útil para serviços
        // downstream).
        builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // Adiciona X-User-Id e X-Tenant-Id se existirem nos claims do token.
        Optional.ofNullable((String) claims.get("X-Correlation-ID")) // Assumindo que X-Correlation-ID no token é o ID
                                                                     // do usuário
                .ifPresent(correlationId -> builder.header("X-User-Id", correlationId));
        Optional.ofNullable((String) claims.get("X-Tenant-ID"))
                .ifPresent(tenantId -> builder.header("X-Tenant-Id", tenantId));

        return builder.build();
    }

    /**
     * Define a resposta como 401 Unauthorized e completa a requisição.
     * 
     * @param exchange O contexto da requisição e resposta.
     * @return Um Mono<Void> que indica a conclusão.
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Define a resposta como 403 Forbidden e completa a requisição.
     * 
     * @param exchange O contexto da requisição e resposta.
     * @return Um Mono<Void> que indica a conclusão.
     */
    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    /**
     * Define a ordem de execução do filtro na cadeia. HIGHEST_PRECEDENCE garante
     * que
     * este filtro seja executado antes da maioria dos outros filtros.
     * 
     * @return A ordem de precedência.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    // --- Classes de Suporte (Exemplo) ---

    /**
     * Exemplo de classe JwtUtil para simular a funcionalidade de JWT.
     * Em uma aplicação real, esta classe conteria a lógica para parsear, validar e
     * gerar JWTs.
     */
    @Component // Marcado como componente para ser injetável
    public static class JwtUtil {
        /**
         * Simula a validação de um token JWT.
         * Em uma implementação real, verificaria assinatura, expiração, etc.
         * 
         * @param token O token a ser validado.
         * @return true se o token for válido, false caso contrário.
         */
        public boolean validateToken(String token) {
            // Lógica de validação real do token (assinatura, expiração, etc.)
            // Para este exemplo, qualquer token não nulo é considerado válido.
            return token != null && !token.isEmpty();
        }

        /**
         * Simula a validação de um token com base em roles e scopes.
         * Em uma implementação real, extrairia claims de role/scope do token e
         * compararia.
         * 
         * @param token          O token a ser validado.
         * @param requiredRole   A role exigida pela rota.
         * @param requiredScopes Os scopes exigidos pela rota.
         * @return true se o token tiver as roles/scopes necessárias, false caso
         *         contrário.
         */
        public boolean validateTokenForRoute(String token, String requiredRole, List<String> requiredScopes) {
            // Lógica de validação real de role e scopes
            // Para este exemplo, sempre retorna true se o token for válido e não houver
            // requisitos específicos.
            // Ou se os requisitos forem "admin" e o token for "valid-admin-token"
            if ("admin".equals(requiredRole) && "valid-admin-token".equals(token)) {
                return true;
            }
            if (requiredRole == null && (requiredScopes == null || requiredScopes.isEmpty())) {
                return true; // Nenhuma restrição de role/scope
            }
            // Simulação: se o token contiver a role necessária ou qualquer um dos scopes
            // Em um cenário real, você decodificaria o token e verificaria os claims.
            return true;
        }

        /**
         * Simula a extração de todos os claims de um token JWT.
         * 
         * @param token O token JWT.
         * @return Um mapa de claims.
         */
        public Map<String, Object> getAllClaimsFromToken(String token) {
            // Lógica real para extrair claims (usando uma biblioteca JWT como JJWT)
            Map<String, Object> claims = new HashMap<>();
            if (token != null && token.contains("admin")) {
                claims.put("X-Correlation-ID", "user-admin-123");
                claims.put("X-Tenant-ID", "tenant-admin");
                claims.put("role", "admin");
            } else if (token != null && token.contains("user")) {
                claims.put("X-Correlation-ID", "user-guest-456");
                claims.put("X-Tenant-ID", "tenant-guest");
                claims.put("role", "user");
            }
            return claims;
        }
    }

    /**
     * Exemplo de classe SecurityProperties para configurar rotas públicas e regras
     * de segurança.
     * Em uma aplicação real, seria carregada via @ConfigurationProperties.
     */
    @Component // Marcado como componente para ser injetável
    public static class SecurityProperties {
        private List<String> publicPaths = List.of("/public/**", "/auth/**");
        private Map<String, RouteConfig> routes = new HashMap<>();

        public SecurityProperties() {
            // Exemplo de configuração de rota:
            // Rota "service-a" exige a role "ADMIN"
            routes.put("service-a", new RouteConfig("ADMIN", List.of("read", "write")));
            // Rota "service-b" não exige role específica, mas exige o scope "view"
            routes.put("service-b", new RouteConfig(null, List.of("view")));
        }

        public List<String> getPublicPaths() {
            return publicPaths;
        }

        public void setPublicPaths(List<String> publicPaths) {
            this.publicPaths = publicPaths;
        }

        public Map<String, RouteConfig> getRoutes() {
            return routes;
        }

        public void setRoutes(Map<String, RouteConfig> routes) {
            this.routes = routes;
        }
    }

    /**
     * Exemplo de classe RouteConfig para definir as propriedades de segurança de
     * uma rota.
     */
    public static class RouteConfig {
        private String requiredRole;
        private List<String> requiredScopes;

        public RouteConfig(String requiredRole, List<String> requiredScopes) {
            this.requiredRole = requiredRole;
            this.requiredScopes = requiredScopes;
        }

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }

        public List<String> getRequiredScopes() {
            return requiredScopes;
        }

        public void setRequiredScopes(List<String> requiredScopes) {
            this.requiredScopes = requiredScopes;
        }
    }
}
