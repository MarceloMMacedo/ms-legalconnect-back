package br.com.legalconnect.auth.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * @class JwtUtil
 * @brief Utilitário para geração, validação e extração de informações de tokens
 *        JWT.
 *
 *        Esta classe é responsável por todas as operações relacionadas a JWTs,
 *        incluindo a criação de tokens, extração de claims (como nome de
 *        usuário e data de expiração),
 *        e validação de tokens.
 */
@Component // Anotação crucial para que o Spring gerencie esta classe como um bean
public class JwtUtil {

    // Chave secreta para assinar os tokens JWT, carregada do application.properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // Tempo de expiração do token JWT em milissegundos, carregado do
    // application.properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * @brief Extrai o nome de usuário (subject) de um token JWT.
     * @param token O token JWT.
     * @return O nome de usuário (geralmente o e-mail) contido no token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * @brief Extrai uma claim específica de um token JWT.
     * @param token          O token JWT.
     * @param claimsResolver Função para resolver a claim desejada dos Claims.
     * @param <T>            O tipo da claim a ser extraída.
     * @return O valor da claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * @brief Gera um token JWT para um UserDetails.
     * @param userDetails Os detalhes do usuário.
     * @return O token JWT gerado.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * @brief Gera um token JWT com claims extras e detalhes do usuário.
     * @param extraClaims Claims adicionais a serem incluídas no token.
     * @param userDetails Os detalhes do usuário.
     * @return O token JWT gerado.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * @brief Constrói o token JWT.
     * @param extraClaims Claims adicionais.
     * @param userDetails Detalhes do usuário.
     * @param expiration  Tempo de expiração do token em milissegundos.
     * @return O token JWT construído.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * @brief Valida um token JWT.
     * @param token       O token JWT a ser validado.
     * @param userDetails Os detalhes do usuário para comparação.
     * @return True se o token é válido, false caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * @brief Verifica se um token JWT está expirado.
     * @param token O token JWT.
     * @return True se o token expirou, false caso contrário.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * @brief Extrai a data de expiração de um token JWT.
     * @param token O token JWT.
     * @return A data de expiração.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @brief Extrai todas as claims de um token JWT.
     * @param token O token JWT.
     * @return Um objeto Claims contendo todas as claims do token.
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
     * @brief Obtém a chave de assinatura decodificada.
     * @return A chave de assinatura.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
