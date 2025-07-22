package br.com.legalconnect.gateway.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import br.com.legalconnect.gateway.config.exception.BusinessException;
import br.com.legalconnect.gateway.config.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
                    var claims = Jwts.parser()
                            .verifyWith(key)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String correlationId = claims.get("X-Correlation-ID", String.class);
                    String tenantId = claims.get("X-Tenant-ID", String.class); // Extrai tenantId do JWT

                    ServerWebExchange mutated = exchange.mutate()
                            .request(builder -> {
                                builder.header("X-Correlation-ID", correlationId != null ? correlationId : "");
                                builder.header("X-Tenant-ID", tenantId != null ? tenantId : "");
                            })
                            .build();

                    return chain.filter(mutated);

                } catch (Exception e) {
                    log.error("❌ Falha ao validar token: " + e.getMessage());
                    throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token inválido");
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
