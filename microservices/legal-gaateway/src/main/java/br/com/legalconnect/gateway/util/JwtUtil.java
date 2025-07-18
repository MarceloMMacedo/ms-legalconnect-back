package br.com.legalconnect.gateway.util;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitário para manipulação e validação de JSON Web Tokens (JWT).
 * <p>
 * Fornece métodos para validar tokens, extrair claims específicos (como userId
 * e tenantId)
 * e gerenciar a chave secreta de assinatura.
 */
@Component
@Slf4j // Anotação Lombok para logging
public class JwtUtil {

    /**
     * A chave secreta para assinatura JWT, injetada do arquivo
     * application.properties.
     * É crucial que esta chave seja a mesma utilizada pelo auth-service para gerar
     * os tokens.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Valida a assinatura e a expiração de um token JWT.
     * <p>
     * Este método tenta fazer o parse do token usando a chave secreta.
     * Se o token for inválido (assinatura incorreta, expirado, malformado, etc.),
     * uma exceção específica do JJWT será lançada.
     *
     * @param token O token JWT a ser validado.
     * @throws SignatureException       Se a assinatura do token for inválida.
     * @throws MalformedJwtException    Se o token não estiver bem formado (JSON
     *                                  inválido, etc.).
     * @throws ExpiredJwtException      Se o token estiver expirado.
     * @throws UnsupportedJwtException  Se o token for de um tipo não suportado (ex:
     *                                  JWS em vez de JWE).
     * @throws IllegalArgumentException Se o token estiver vazio, nulo ou não tiver
     *                                  claims.
     */
    public void validateToken(final String token) {
        try {
            // Constrói um parser JWT com a chave de assinatura e tenta fazer o parse do
            // token.
            Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token);
        } catch (SignatureException e) {
            log.error("Assinatura JWT inválida: {}", e.getMessage());
            throw new SignatureException("Assinatura JWT inválida", e);
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
            throw new MalformedJwtException("Token JWT malformado", e);
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
            // Relança a exceção para que o filtro possa tratá-la especificamente.
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "Token JWT expirado", e);
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
            throw new UnsupportedJwtException("Token JWT não suportado", e);
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vazio ou nulo: {}", e.getMessage());
            throw new IllegalArgumentException("Token JWT vazio ou nulo", e);
        }
    }

    /**
     * Extrai todas as claims (conteúdo) de um token JWT.
     * Este método assume que o token já foi validado ou que a validação de
     * assinatura
     * será tratada pelo caller.
     *
     * @param token O token JWT.
     * @return As claims (um mapa de chave-valor) contidas no corpo do token.
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extrai uma claim específica de um token JWT usando uma função resolutora.
     * Permite extrair qualquer claim do token de forma genérica.
     *
     * @param token          O token JWT.
     * @param claimsResolver Uma função que recebe as {@link Claims} e retorna o
     *                       valor da claim desejada.
     * @param <T>            O tipo da claim a ser extraída.
     * @return A claim extraída do token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai o ID do usuário (userId) das claims do JWT.
     * Assume que o userId está presente como uma claim customizada chamada
     * "userId".
     *
     * @param claims As claims do JWT.
     * @return O ID do usuário como String.
     */
    public String extractUserId(Claims claims) {
        return claims.get("userId", String.class);
    }

    /**
     * Extrai o ID do tenant (tenantId) das claims do JWT.
     * Assume que o tenantId está presente como uma claim customizada chamada
     * "tenantId".
     *
     * @param claims As claims do JWT.
     * @return O ID do tenant como String.
     */
    public String extractTenantId(Claims claims) {
        return claims.get("tenantId", String.class);
    }

    /**
     * Verifica se o token JWT está expirado.
     *
     * @param token O token JWT.
     * @return {@code true} se o token estiver expirado, {@code false} caso
     *         contrário.
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração de um token JWT.
     *
     * @param token O token JWT.
     * @return A data de expiração do token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Gera a chave de assinatura a partir da string secreta configurada.
     * A chave é decodificada de Base64 e usada para criar uma chave HMAC SHA.
     *
     * @return A {@link Key} utilizada para assinar e verificar o JWT.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}