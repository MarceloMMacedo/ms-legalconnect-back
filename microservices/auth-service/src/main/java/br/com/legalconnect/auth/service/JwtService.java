package br.com.legalconnect.auth.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * @class JwtService
 * @brief Serviço para geração, validação e extração de informações de tokens
 *        JWT.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Extrai o nome de usuário (subject) do token JWT.
     * 
     * @param token O token JWT.
     * @return O nome de usuário.
     */
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

    /**
     * Extrai o ID do usuário dos claims do token JWT.
     * 
     * @param token O token JWT.
     * @return O ID do usuário.
     */
    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("userId", Long.class));
        } catch (Exception e) {
            log.warn("Erro ao extrair userId do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrai o ID do tenant dos claims do token JWT.
     * 
     * @param token O token JWT.
     * @return O ID do tenant.
     */
    public Long extractTenantId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("tenantId", Long.class));
        } catch (Exception e) {
            log.warn("Erro ao extrair tenantId do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gera um token JWT para um usuário.
     * 
     * @param userDetails Detalhes do usuário.
     * @return O token JWT gerado.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Gera um token JWT com claims extras para um usuário.
     * 
     * @param extraClaims Claims adicionais.
     * @param userDetails Detalhes do usuário.
     * @return O token JWT gerado.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        log.debug("Gerando token JWT para o usuário: {}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Gera um refresh token JWT para um usuário.
     * 
     * @param userDetails Detalhes do usuário.
     * @return O refresh token JWT gerado.
     */
    public String generateRefreshToken(
            UserDetails userDetails) {
        log.debug("Gerando refresh token para o usuário: {}", userDetails.getUsername());
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    /**
     * Constrói o token JWT.
     * 
     * @param extraClaims Claims adicionais.
     * @param userDetails Detalhes do usuário.
     * @param expiration  Tempo de expiração em milissegundos.
     * @return O token JWT construído.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // O email do usuário será o subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida se um token JWT é válido para um determinado usuário.
     * 
     * @param token       O token JWT.
     * @param userDetails Detalhes do usuário.
     * @return True se o token é válido, false caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = (username != null && username.equals(userDetails.getUsername()))
                    && !isTokenExpired(token);
            if (!isValid) {
                log.warn("Validação de token falhou para usuário: {}. Token válido: {}, Token expirado: {}",
                        username, (username != null && username.equals(userDetails.getUsername())),
                        isTokenExpired(token));
            }
            return isValid;
        } catch (Exception e) {
            log.error("Erro durante a validação do token: {}", e.getMessage(), e);
            return false;
        }
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

    /**
     * Extrai todas as claims do token JWT.
     * 
     * @param token O token JWT.
     * @return As claims do token.
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
     * Obtém a chave de assinatura para o JWT.
     * 
     * @return A chave de assinatura.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}