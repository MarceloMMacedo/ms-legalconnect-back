package br.com.legalconnect.gateway.util;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitário para operações com JWT (JSON Web Token).
 * Inclui métodos para extrair claims, validar e verificar a expiração de
 * tokens.
 */
@Component
public class JwtUtil {

    // Chave secreta para assinar e validar tokens JWT, injetada do
    // application.properties.
    @Value("${application.security.jwt.secret-key}")
    private String secret;

    /**
     * Obtém a chave de assinatura a partir da string secreta.
     * 
     * @return A SecretKey para assinatura e verificação.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrai todos os claims de um token JWT.
     * 
     * @param token O token JWT.
     * @return Um mapa contendo todos os claims do token.
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extrai um claim específico de um token JWT usando uma função resolver.
     * 
     * @param token          O token JWT.
     * @param claimsResolver Função para resolver o claim desejado.
     * @param <T>            O tipo do claim.
     * @return O valor do claim.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = (Claims) getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtém a data de expiração de um token JWT.
     * 
     * @param token O token JWT.
     * @return A data de expiração.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Verifica se um token JWT está expirado.
     * 
     * @param token O token JWT.
     * @return true se o token estiver expirado, false caso contrário.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Valida um token JWT.
     * Captura diferentes exceções de validação de JWT para logar e retornar false.
     * 
     * @param token O token JWT a ser validado.
     * @return true se o token for válido, false caso contrário.
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            // Logger.error("Assinatura JWT inválida: {}", e.getMessage());
            System.err.println("Assinatura JWT inválida: " + e.getMessage());
        } catch (MalformedJwtException e) {
            // Logger.error("Token JWT malformado: {}", e.getMessage());
            System.err.println("Token JWT malformado: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            // Logger.error("Token JWT expirado: {}", e.getMessage());
            System.err.println("Token JWT expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Logger.error("Token JWT não suportado: {}", e.getMessage());
            System.err.println("Token JWT não suportado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Logger.error("String JWT compacta vazia: {}", e.getMessage());
            System.err.println("String JWT compacta vazia: " + e.getMessage());
        }
        return false;
    }
}