package br.com.legalconnect.gateway.util;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Filtro de Gateway para autorização baseada em JWT e roles.
 * Este filtro intercepta as requisições, valida o token JWT e verifica as roles
 * do usuário antes de permitir o acesso a rotas protegidas.
 */
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthorizationHeaderFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
        // Coloque aqui configurações específicas para este filtro, se necessário.
        // Por exemplo, uma lista de rotas que exigem uma role específica.
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Verificar se a rota requer autorização (e.g., ignorar rotas públicas como
            // /auth/login, /auth/register)
            // Você pode expandir esta lógica para usar um matcher de caminho mais
            // sofisticado
            // ou uma lista de rotas públicas/protegidas.
            // if (isPublicPath(request.getPath().toString())) {
            // return chain.filter(exchange); // Permite o acesso a rotas públicas sem
            // verificação de token
            // }

            // 2. Verificar se o cabeçalho Authorization está presente
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Cabeçalho de autorização ausente", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String jwt = null;

            // Extrair o token JWT (assumindo formato "Bearer <token>")
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
            }

            if (jwt == null) {
                return onError(exchange, "Token JWT inválido ou ausente", HttpStatus.UNAUTHORIZED);
            }

            // 3. Validar o token JWT
            if (!jwtUtil.validateToken(jwt)) {
                return onError(exchange, "Token JWT inválido ou expirado", HttpStatus.UNAUTHORIZED);
            }

            // 4. Extrair roles e realizar verificação de autorização para rotas específicas
            List<String> userRoles = jwtUtil.extractRoles(jwt);
            String requestPath = request.getPath().toString();

            // Exemplo de regra de autorização: Apenas ADMIN pode acessar
            if (userRoles == null || !userRoles.contains("ROLE_ADMIN")) {
                return onError(exchange, "Acesso negado. Requer ROLE_ADMIN.", HttpStatus.FORBIDDEN);
            }

            // Adicione mais regras de autorização aqui conforme a necessidade
            // Ex: if (requestPath.startsWith("/api/admin/") &&
            // !userRoles.contains("ROLE_ADMIN")) { ... }

            // 5. Adicionar informações do usuário (se necessário) ao cabeçalho da
            // requisição para serviços downstream
            // Isso pode ser útil se o serviço de destino precisar do ID do usuário, etc.
            // Por exemplo: request.mutate().header("X-User-Id",
            // jwtUtil.extractUsername(jwt)).build();

            return chain.filter(exchange); // Continua para o próximo filtro ou rota
        };
    }

    /**
     * Método auxiliar para lidar com erros e retornar uma resposta de erro.
     * 
     * @param exchange   O ServerWebExchange.
     * @param err        Mensagem de erro.
     * @param httpStatus Status HTTP a ser retornado.
     * @return Mono<Void> indicando a conclusão da resposta.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // Opcional: Adicionar a mensagem de erro no corpo da resposta
        // return
        // response.writeWith(Mono.just(response.bufferFactory().wrap(err.getBytes())));
        return response.setComplete();
    }

    /**
     * Define quais caminhos são públicos e não requerem autenticação/autorização no
     * Gateway.
     * 
     * @param path O caminho da requisição.
     * @return true se o caminho é público, false caso contrário.
     */
    // private boolean isPublicPath(String path) {
    // // Adapte esta lista para as suas rotas públicas
    // return path.startsWith("/auth/login") || path.startsWith("/auth/register");
    // }
}