package br.com.legalconnect.gateway.config;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import br.com.legalconnect.gateway.util.JwtUtil;
import reactor.core.publisher.Mono;

@Component("CustomJwtTokenFilter") // Nome do bean para uso no application.yml
public class JwtTokenFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    public JwtTokenFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Verifica se a rota é pública (ex: /auth/** ou Swagger)
        // Isso é mais robusto fazer com Spring Security WebFilterChain
        // Mas para este exemplo, podemos verificar o path
        if (isPublicPath(request.getPath().value())) {
            return chain.filter(exchange);
        }

        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String token = authHeaders.get(0).substring(7); // Remove "Bearer "

        if (!jwtUtil.validateToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // Extrai e remove claims específicos
        Map<String, Object> claims = jwtUtil.getAllClaimsFromToken(token);
        String userId = (String) claims.get("userId");
        String tenantId = (String) claims.get("tenantId");

        // Remove os claims do token original (se necessário para nova validação
        // downstream)
        // Isso é mais complexo pois você precisaria gerar um novo token sem esses
        // claims.
        // Uma abordagem mais comum é manter o token original e propagar os claims
        // em headers separados para os microserviços.
        // Para este projeto, vamos propagar os claims como headers.

        ServerHttpRequest.Builder builder = request.mutate();
        if (userId != null) {
            builder.header("X-User-Id", userId);
        }
        if (tenantId != null) {
            builder.header("X-Tenant-Id", tenantId);
        }

        // Para evitar que o token completo seja enviado para os serviços downstream
        // Se você precisar do token completo downstream, considere remover essa linha
        // ou validar a necessidade.
        builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token); // Mantém o token original

        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    // Helper para verificar se o path é público
    private boolean isPublicPath(String path) {
        // Rotas que devem ser liberadas sem autenticação
        return path.startsWith("/auth/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");
    }
}
