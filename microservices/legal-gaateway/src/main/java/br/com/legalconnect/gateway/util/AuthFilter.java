package br.com.legalconnect.gateway.util;

import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException; // Importe para lidar com a exceção de assinatura inválida
import reactor.core.publisher.Mono;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    // Chave secreta para validação do JWT (idealmente carregada de um local seguro)
    @Value("${jwt.secret:suaChaveSecretaMuitoSegura}") // Substitua por uma chave real e segura
    private String jwtSecret;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Verificar se a requisição possui o cabeçalho Authorization
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = Objects
                    .requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
            String token = authHeader.replace("Bearer ", "");

            try {
                // 2. Validar o token e extrair os claims
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSecret.getBytes()) // Use sua chave secreta
                        .parseClaimsJws(token)
                        .getBody();

                // Exemplo de como extrair um user_id e correlation_id
                // Adapte os nomes das chaves ('sub', 'correlationId') conforme seu token JWT
                String userId = claims.getSubject(); // 'sub' é um claim comum para o assunto/usuário
                String correlationId = claims.containsKey("correlationId") ? claims.get("correlationId", String.class)
                        : UUID.randomUUID().toString(); // Gerar um novo se não houver

                // 3. Adicionar os claims aos cabeçalhos da requisição antes de rotear
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-Correlation-ID", correlationId)
                                .header("X-User-Id", userId))
                        .build();

                return chain.filter(mutatedExchange);

            } catch (SignatureException e) {
                // Token com assinatura inválida
                System.err.println("Invalid JWT signature: " + e.getMessage());
                return this.onError(exchange, "Invalid JWT Signature", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                // Outros erros de parsing ou validação do token
                System.err.println("Invalid or expired JWT token: " + e.getMessage());
                return this.onError(exchange, "Unauthorized Access", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        // Nada a configurar por enquanto para este filtro, mas útil para filtros mais
        // complexos.
    }
}