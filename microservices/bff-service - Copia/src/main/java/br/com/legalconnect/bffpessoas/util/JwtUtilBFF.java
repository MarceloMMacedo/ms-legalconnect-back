package br.com.legalconnect.bffpessoas.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * @class JwtUtilBFF
 * @brief Utilitário para manipulação de JSON Web Tokens (JWT) no BFF.
 *
 * Esta classe é responsável por extrair informações (claims) e validar
 * parcialmente tokens JWT recebidos do frontend. Ela não gera tokens,
 * apenas os decodifica e verifica sua integridade.
 */
@Service
public class JwtUtilBFF {

    private static final Logger log = LoggerFactory.getLogger(JwtUtilBFF.class);

    // Chave secreta para verificar os JWTs, injetada do application.properties
    // DEVE SER A MESMA CHAVE USADA NO AUTH-SERVICE PARA ASSINAR OS TOKENS.
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * @brief Extrai o nome de usuário (subject) do token JWT.
     * @param token O token JWT.
     * @return O nome de usuário (geralmente o e-mail do usuário) contido no token, ou null se houver erro.
     */
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            log.warn("Erro ao extrair username do token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * @brief Extrai o ID do usuário (claim "userId") do token JWT.
     * @param token O token JWT.
     * @return O ID do usuário (Long) contido no token, ou null se não encontrado ou houver erro.
     */
    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("userId", Long.class));
        } catch (Exception e) {
            log.warn("Erro ao extrair userId do token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * @brief Extrai o ID do tenant (claim "tenantId") do token JWT.
     * @param token O token JWT.
     * @return O ID do tenant (Long) contido no token, ou null se não encontrado ou houver erro.
     */
    public Long extractTenantId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("tenantId", Long.class));
        } catch (Exception e) {
            log.warn("Erro ao extrair tenantId do token JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * @brief Extrai uma claim específica do token JWT.
     * @param token O token JWT.
     * @param claimsResolver Função para resolver a claim a partir do objeto `Claims`.
     * @return O valor da claim extraída.
     * @tparam T O tipo da claim a ser extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * @brief Valida um token JWT.
     *
     * Verifica se o nome de usuário no token corresponde ao `UserDetails` fornecido
     * e se o token não expirou.
     * @param token O token JWT a ser validado.
     * @param userDetails Os detalhes do usuário para quem o token foi emitido.
     * @return `true` se o token for válido, `false` caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.debug("Validando token JWT para o usuário: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);
            boolean isValid = (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
            if (!isValid) {
                log.warn("Token JWT inválido para o usuário {}. Username não corresponde ou token expirou.", userDetails.getUsername());
            }
            return isValid;
        } catch (SignatureException e) {
            log.error("Assinatura JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("String JWT vazia ou nula: {}", e.getMessage());
        }
        return false;
    }

    /**
     * @brief Verifica se o token JWT expirou.
     * @param token O token JWT.
     * @return `true` se o token expirou, `false` caso contrário.
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        boolean expired = expiration.before(new Date());
        if (expired) {
            log.debug("Token JWT expirado em: {}", expiration);
        }
        return expired;
    }

    /**
     * @brief Extrai a data de expiração do token JWT.
     * @param token O token JWT.
     * @return A data de expiração.
     */
    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @brief Extrai todas as claims do token JWT.
     *
     * Realiza o parsing do token usando a chave de assinatura.
     * @param token O token JWT.
     * @return As claims (payload) do token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @brief Obtém a chave de assinatura decodificada para o JWT.
     *
     * Decodifica a chave secreta base64 configurada para uso na verificação.
     * @return A chave de assinatura.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}