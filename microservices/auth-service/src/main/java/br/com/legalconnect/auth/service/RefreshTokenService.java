package br.com.legalconnect.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.user.entity.RefreshToken;
import br.com.legalconnect.user.entity.User;
import br.com.legalconnect.user.repository.RefreshTokenRepository;
import jakarta.persistence.EntityManager; // Importar EntityManager

/**
 * @class RefreshTokenService
 * @brief Serviço para gerenciamento de Refresh Tokens.
 *
 *        Responsável por criar, validar e invalidar refresh tokens, garantindo
 *        a
 *        renovação segura de sessões de usuário.
 */
@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired // Injetar EntityManager
    private EntityManager entityManager;
    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshTokenExpirationMs;

    /**
     * @brief Cria e salva um novo Refresh Token para um usuário.
     *
     * @param user O usuário para o qual o Refresh Token será criado.
     * @return O RefreshToken criado e persistido.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Criando refresh token para o usuário: {}", user.getEmail());
        // Invalida qualquer refresh token existente para este usuário para garantir
        // apenas um por vez
        refreshTokenRepository.findByUser(user).ifPresent(existingToken -> {
            log.debug("Deletando refresh token existente para o usuário: {}", user.getEmail());
            refreshTokenRepository.delete(existingToken);
            // Força o Hibernate a sincronizar a deleção com o banco de dados imediatamente
            entityManager.flush();
        });

        // Gera um novo token único
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(refreshTokenExpirationMs, ChronoUnit.MILLIS);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiraEm(expiryDate)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token criado e salvo com sucesso para o usuário: {}", user.getEmail());
        return refreshToken;
    }

    /**
     * @brief Busca um Refresh Token pelo seu valor.
     *
     * @param token O valor do Refresh Token.
     * @return Um `Optional` contendo o RefreshToken, se encontrado.
     */
    public Optional<RefreshToken> findByToken(String token) {
        log.debug("Buscando refresh token pelo valor: {}", token);
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * @brief Verifica se um Refresh Token expirou.
     *
     * @param token O RefreshToken a ser verificado.
     * @return O RefreshToken se não estiver expirado.
     * @throws BusinessException se o Refresh Token estiver expirado.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        log.debug("Verificando expiração do refresh token para o usuário: {}", token.getUser().getEmail());
        if (token.getExpiraEm().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token); // Remove o token expirado
            entityManager.flush(); // Força o Hibernate a sincronizar a deleção
            log.warn("Refresh token expirado para o usuário: {}. Token deletado.", token.getUser().getEmail());
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED,
                    "Refresh token expirado. Por favor, faça login novamente.");
        }
        log.debug("Refresh token para o usuário {} ainda é válido.", token.getUser().getEmail());
        return token;
    }

    /**
     * @brief Deleta um Refresh Token pelo seu valor.
     *
     * @param token O valor do Refresh Token a ser deletado.
     * @throws BusinessException se o Refresh Token não for encontrado.
     */
    @Transactional
    public void deleteByToken(String token) {
        log.info("Deletando refresh token pelo valor: {}", token);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Tentativa de deletar refresh token não encontrado: {}", token);

                    return new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh token não encontrado.");
                });
        refreshTokenRepository.delete(refreshToken);
        entityManager.flush(); // Força o Hibernate a sincronizar a deleção
        log.info("Refresh token deletado com sucesso para o usuário: {}", refreshToken.getUser().getEmail());
    }

    /**
     * @brief Deleta um Refresh Token pelo usuário associado.
     *
     * @param user O usuário cujo Refresh Token será deletado.
     */
    @Transactional
    public void deleteByUser(User user) {
        log.info("Deletando refresh token para o usuário: {}", user.getEmail());
        refreshTokenRepository.findByUser(user).ifPresent(refreshToken -> {
            refreshTokenRepository.delete(refreshToken);
            entityManager.flush(); // Força o Hibernate a sincronizar a deleção
            log.info("Refresh token deletado com sucesso para o usuário: {}", user.getEmail());
        });
    }
}