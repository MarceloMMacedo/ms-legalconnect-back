package br.com.legalconnect.gateway.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitário para manipulação de tokens JWT no Gateway.
 * Esta classe é responsável por decodificar e validar tokens JWT.
 * A chave de assinatura DEVE ser a mesma utilizada para gerar o token no
 * AUTH-SERVICE.
 */
@Component
public class JwtUtil {

    // A chave secreta deve ser a mesma usada no AUTH-SERVICE para assinar o JWT
    // É crucial que esta chave seja tratada como um segredo e não seja exposta.
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    private Key getSigningKey() {
        // Gera a chave a partir do segredo. Use Keys.hmacShaKeyFor(secret.getBytes())
        // para chaves de 256 bits ou mais.
        // Certifique-se de que 'secret' é longo o suficiente (mínimo 32 caracteres para
        // HS256).
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extrai todas as claims (informações) do token JWT.
     * 
     * @param token O token JWT.
     * @return As claims contidas no token.
     */
    public Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrai uma claim específica do token JWT.
     * 
     * @param token          O token JWT.
     * @param claimsResolver Função para resolver a claim desejada.
     * @param <T>            Tipo da claim.
     * @return O valor da claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai o nome de usuário (subject) do token JWT.
     * 
     * @param token O token JWT.
     * @return O nome de usuário.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai a data de expiração do token JWT.
     * 
     * @param token O token JWT.
     * @return A data de expiração.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Verifica se o token JWT é válido (não expirado e com assinatura válida).
     * 
     * @param token O token JWT.
     * @return true se o token é válido, false caso contrário.
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Logar a exceção para depuração (ex: token malformado, assinatura inválida)
            System.err.println("Erro ao validar token JWT: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica se o token JWT está expirado.
     * 
     * @param token O token JWT.
     * @return true se o token expirou, false caso contrário.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai as roles (cargos/permissões) do token JWT.
     * Assume que as roles estão em uma claim chamada "roles" ou similar.
     * É crucial que o AUTH-SERVICE inclua esta claim no JWT.
     * 
     * @param token O token JWT.
     * @return Uma lista de strings representando as roles.
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        // Assumindo que as roles são armazenadas como uma lista de strings na claim
        // "roles"
        // Adapte "roles" para o nome real da claim que seu AUTH-SERVICE usa.
        return (List<String>) claims.get("roles", List.class);
    }
}