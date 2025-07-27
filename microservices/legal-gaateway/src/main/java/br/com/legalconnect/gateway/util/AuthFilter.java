package br.com.legalconnect.gateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import br.com.legalconnect.gateway.config.exception.BusinessException;
import br.com.legalconnect.gateway.config.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component

public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public AuthFilter(

    ) {
        super(Config.class);

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "Token não encontrado");
                }

                String token = authHeader.substring(7);

                if (token == null || token.isBlank()) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "Token não encontrado");
                }

                Claims claims = extractAllClaims(token);
                if (isTokenExpired(token)) {
                    throw new BusinessException(ErrorCode.TOKEN_EXPIRED, "Token expirado");
                }
                List<String> userRoles = (List<String>) claims.get("roles", List.class);

                String correlationId = claims.get("X-Correlation-ID", String.class);

                String tenantId = claims.get("X-Tenant-ID", String.class);

                if (correlationId == null || correlationId.isBlank()) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "X-Correlation-ID não encontrado no token");
                }

                if (tenantId == null || tenantId.isBlank()) {
                    throw new BusinessException(ErrorCode.TENANT_NOT_FOUND, "tenantId não encontrado no token");
                }

                if (userRoles == null || userRoles.isEmpty()) {
                    throw new BusinessException(ErrorCode.USER_NOT_FOUND, "Role não encontrada no token");
                }

                ServerWebExchange mutated = exchange.mutate()
                        .request(builder -> {
                            builder.header("X-Correlation-ID", correlationId);
                            builder.header("X-Tenant-ID", tenantId);
                            builder.header("X-User-Roles", String.join(",", userRoles));

                        })
                        .build();

                return chain.filter(mutated);

            } catch (Exception e) {
                log.error("Erro ao processar token JWT: {}", e.getMessage());
                // if (e instanceof BusinessException) {
                // throw (BusinessException) e;
                // }
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token inválido ou malformado");
            }
        };
    }

    public static class Config {
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica se o token JWT expirou.
     * 
     * @param token O token JWT.
     * @return True se o token expirou, false caso contrário.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.debug("Token expirado em: {}", expiration);
        }
        return expired;
    }

    /**
     * Extrai a data de expiração do token JWT.
     * 
     * @param token O token JWT.
     * @return A data de expiração.
     */
    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.warn("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrai uma claim específica do token JWT.
     * 
     * @param token          O token JWT.
     * @param claimsResolver Função para resolver a claim.
     * @param <T>            Tipo da claim.
     * @return O valor da claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

}