package br.com.legalconnect.gateway.filter;

import br.com.legalconnect.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

/**
 * Filtro de Gateway para autenticação e autorização via JWT.
 * <p>
 * Este filtro intercepta todas as requisições de entrada, valida o token JWT presente
 * no cabeçalho 'Authorization' (se presente) e injeta os claims 'userId' e 'tenantId'
 * nos cabeçalhos da requisição (X-User-ID e X-Tenant-ID) antes de roteá-la para o
 * microsserviço de destino.
 * <p>
 * Permite que certos caminhos (configurados em `jwt.public.paths` no `application.properties`)
 * sejam acessados publicamente sem a necessidade de um JWT prévio.
 */
@Component
@Slf4j // Anotação Lombok para logging
public class JwtAuthenticationGatewayFilter implements GatewayFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final List<String> publicPaths;

    /**
     * Construtor do filtro de autenticação JWT.
     *
     * @param jwtUtil Instância de {@link JwtUtil} para validação e extração de JWTs.
     * @param publicPaths Lista de caminhos que são considerados públicos e não exigem JWT.
     */
    public JwtAuthenticationGatewayFilter(JwtUtil jwtUtil, List<String> publicPaths) {
        this.jwtUtil = jwtUtil;
        this.publicPaths = publicPaths;
    }

    /**
     * Implementação do método de filtragem reativa.
     * <p>
     * O fluxo de processamento é o seguinte:
     * 1. Obtém a requisição e o caminho da URI.
     * 2. Verifica se o caminho da requisição é um caminho público.
     * Se for, o filtro é ignorado e a requisição prossegue na cadeia de filtros.
     * 3. Para caminhos não públicos, verifica a presença do cabeçalho 'Authorization'.
     * Se ausente, retorna erro 401 (Unauthorized).
     * 4. Extrai o token "Bearer" do cabeçalho.
     * 5. Tenta validar o token JWT usando {@link JwtUtil}.
     * 6. Se o token for válido, extrai o 'userId' e 'tenantId' dos claims.
     * 7. Adiciona 'X-User-ID' e 'X-Tenant-ID' como novos cabeçalhos na requisição.
     * 8. A requisição modificada é então passada para o próximo filtro na cadeia.
     * 9. Em caso de qualquer falha na validação do JWT, retorna erro 401 (Unauthorized).
     *
     * @param exchange O contexto da troca web, contendo a requisição e a resposta reativas.
     * @param chain A cadeia de filtros para processamento subsequente.
     * @return Um {@link Mono<Void>} indicando a conclusão do processamento do filtro.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Predicado para verificar se o caminho da requisição é público.
        // Ele verifica se o caminho da requisição começa com qualquer um dos caminhos públicos configurados.
        Predicate<String> isPublic = uri -> publicPaths.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath.replace("/**", "")));

        // Se o caminho é público, permite a requisição sem validação de JWT.
        if (isPublic.test(path)) {
            log.info("Requisição para caminho público: {} - pulando validação JWT.", path);
            return chain.filter(exchange);
        }

        // Para caminhos não públicos, exige e valida o JWT.
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Cabeçalho Authorization ausente para o caminho: {}", path);
            return this.onError(exchange, "Cabeçalho Authorization está ausente", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = null;

        // Verifica o formato do cabeçalho Authorization (deve ser "Bearer <token>").
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove o prefixo "Bearer "
        } else {
            log.warn("Formato de cabeçalho Authorization inválido para o caminho: {}", path);
            return this.onError(exchange, "Formato de cabeçalho Authorization inválido", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Valida o token JWT (assinatura e expiração).
            jwtUtil.validateToken(token);
            // Extrai todas as claims do token.
            Claims claims = jwtUtil.getAllClaimsFromToken(token);

            // Extrai o userId e tenantId das claims.
            String userId = jwtUtil.extractUserId(claims);
            String tenantId = jwtUtil.extractTenantId(claims);

            // Verifica se userId e tenantId foram extraídos com sucesso.
            if (userId == null || tenantId == null) {
                log.error("userId ou tenantId ausentes nas claims do JWT para o caminho: {}", path);
                return this.onError(exchange, "userId ou tenantId ausentes no JWT", HttpStatus.UNAUTHORIZED);
            }

            // Adiciona userId e tenantId como novos cabeçalhos na requisição.
            // Estes cabeçalhos serão acessíveis pelos microsserviços de backend.
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-ID", userId)
                    .header("X-Tenant-ID", tenantId)
                    .build();

            log.info("JWT validado para usuário: {} (tenant: {}) no caminho: {}", userId, tenantId, path);
            // Continua a cadeia de filtros com a requisição modificada.
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado para o caminho: {}. Erro: {}", path, e.getMessage());
            return this.onError(exchange, "Token JWT expirado", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Falha na validação do JWT para o caminho: {}. Erro: {}", path, e.getMessage());
            return this.onError(exchange, "Falha na validação do JWT: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Captura qualquer outra exceção inesperada durante o processamento do JWT.
            log.error("Erro inesperado durante a validação do JWT para o caminho: {}. Erro: {}", path, e.getMessage());
            return this.onError(exchange, "Erro interno durante a validação do JWT", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Método auxiliar para construir e retornar uma resposta de erro.
     * Define o status HTTP da resposta e registra o erro.
     *
     * @param exchange O contexto da troca web.
     * @param err      Mensagem de erro a ser logada e, opcionalmente, retornada no corpo da resposta.
     * @param httpStatus Status HTTP a ser definido na resposta.
     * @return Um {@link Mono<Void>} representando a resposta de erro completa.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // Opcional: Adicionar corpo de erro com mais detalhes ou um formato JSON para o cliente.
        // Ex: response.getHeaders().add("Content-Type", "application/json");
        //     return response.writeWith(Mono.just(response.bufferFactory().wrap(err.getBytes())));
        log.error("Erro no API Gateway: {}. Status: {}", err, httpStatus);
        return response.setComplete(); // Completa a resposta sem um corpo explícito.
    }

    /**
     * Define a ordem de execução do filtro na cadeia de filtros do Gateway.
     * <p>
     * Um valor mais baixo indica maior precedência. Retornar um valor alto (como {@code -1})
     * garante que este filtro seja executado bem cedo, antes da maioria dos outros filtros
     * do Spring Cloud Gateway, permitindo que a autenticação ocorra antes do roteamento.
     *
     * @return A ordem do filtro.
     */
    @Override
    public int getOrder() {
        return -1; // Garante que este filtro seja executado com alta precedência.
    }
}