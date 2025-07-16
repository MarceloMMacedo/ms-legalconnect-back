package br.com.legalconnect.auth.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * @class JwtUtil
 * @brief Utilitário para manipulação de JSON Web Tokens (JWT).
 *
 * Esta classe é responsável por gerar, extrair informações e validar
 * JWTs,
 * que são usados para autenticação e autorização na API. A chave secreta
 * e o tempo de expiração são configuráveis via `application.properties`.
 */
@Service
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class); // Instância do Logger

    // Chave secreta para assinar e verificar os JWTs, injetada do application.properties
    // É crucial que esta chave seja forte e mantida em segredo.
    @Value("${jwt.secret}")
    private String secretKey;

    // Tempo de expiração do JWT em milissegundos (ex: 3600000ms = 1 hora)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * @brief Extrai o nome de usuário (subject) do token JWT.
     * @param token O token JWT.
     * @return O nome de usuário (geralmente o e-mail do usuário) contido no token.
     */
    public String extractUsername(String token) {
        log.debug("Extraindo username do token JWT.");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * @brief Extrai uma claim específica do token JWT.
     * @param token          O token JWT.
     * @param claimsResolver Função para resolver a claim a partir do objeto
     * `Claims`.
     * @return O valor da claim extraída.
     * @tparam T O tipo da claim a ser extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * @brief Gera um token JWT para um `UserDetails`.
     *
     * Este método é um atalho para gerar um token sem claims extras.
     *
     * @param userDetails Os detalhes do usuário (implementação de `UserDetails`).
     * @return O token JWT gerado.
     */
    public String generateToken(UserDetails userDetails) {
        log.info("Gerando token JWT para o usuário: {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * @brief Gera um token JWT com claims extras.
     *
     * Inclui o nome de usuário (e-mail), roles e, se for um
     * `CustomUserDetails`,
     * o `tenantId` como claims no token. Define a data de emissão e
     * expiração.
     *
     * @param extraClaims Claims adicionais para incluir no token (ex: "user_type").
     * @param userDetails Os detalhes do usuário (implementação de `UserDetails`).
     * @return O token JWT gerado e assinado.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        // Adiciona as roles do usuário como uma claim "roles"
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()));

        // Adiciona o tenant_id como claim se o UserDetails for uma instância de
        // CustomUserDetails
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            if (customUserDetails.getTenantId() != null) {
                extraClaims.put("tenant_id", customUserDetails.getTenantId().toString());
            }
        }

        String token = Jwts
                .builder()
                .setClaims(extraClaims) // Define as claims personalizadas
                .setSubject(userDetails.getUsername()) // O e-mail do usuário como subject (identificador principal)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Data de emissão do token
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Data de expiração do token
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Assina o token com a chave secreta e algoritmo HS256
                .compact(); // Constrói e compacta o token JWT
        log.debug("Token JWT gerado com sucesso para o usuário: {}", userDetails.getUsername());
        return token;
    }

    /**
     * @brief Valida um token JWT.
     *
     * Verifica se o nome de usuário no token corresponde ao `UserDetails`
     * fornecido
     * e se o token não expirou.
     *
     * @param token       O token JWT a ser validado.
     * @param userDetails Os detalhes do usuário para quem o token foi emitido.
     * @return `true` se o token for válido, `false` caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.debug("Validando token JWT para o usuário: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
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
            log.error("String JWT vazia: {}", e.getMessage());
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
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @brief Extrai todas as claims do token JWT.
     *
     * Realiza o parsing do token usando a chave de assinatura.
     *
     * @param token O token JWT.
     * @return As claims (payload) do token.
     */
    private Claims extractAllClaims(String token) {

        return Jwts
                .parserBuilder() // Usar parserBuilder para construir o parser
                .setSigningKey(getSignInKey()) // Define a chave para verificar a assinatura
                .build()
                .parseClaimsJws(token) // Faz o parsing e valida a assinatura
                .getBody(); // Retorna o corpo (claims) do token
    }

    /**
     * @brief Obtém a chave de assinatura decodificada para o JWT.
     *
     * Decodifica a chave secreta base64 configurada para uso na
     * assinatura/verificação.
     *
     * @return A chave de assinatura.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}