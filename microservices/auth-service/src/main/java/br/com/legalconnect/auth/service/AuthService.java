package br.com.legalconnect.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.legalconnect.auth.dto.AuthResponse;
import br.com.legalconnect.auth.dto.LoginRequestDTO;
import br.com.legalconnect.auth.dto.RefreshTokenRequestDTO;
import br.com.legalconnect.common.common_lib.BaseResponse;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.user.entity.User;
import br.com.legalconnect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * @class AuthService
 * @brief Serviço de lógica de negócios para autenticação e refresh de tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    /*
     * /
     * Autentica um usuário e gera tokens JWT.
     * Inclui id do usuário e id do tenant nos claims do JWT.
     * * @param request DTO de requisição de login.
     * 
     * @return DTO de resposta de login com tokens.
     * 
     * @throws ResponseStatusException se o usuário não for encontrado ou as
     * credenciais forem inválidas.
     */
    @Transactional
    public BaseResponse<AuthResponse> authenticate(LoginRequestDTO request) {
        log.debug("Iniciando autenticação para o e-mail: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()));
            log.info("Credenciais válidas para o e-mail: {}", request.getEmail());
        } catch (Exception e) {
            log.warn("Falha na autenticação para o e-mail: {}. Erro: {}", request.getEmail(), e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Credenciais inválidas.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado no banco de dados para o e-mail: {}", request.getEmail());
                    return new BusinessException(ErrorCode.USER_NOT_FOUND,
                            "Usuário não encontrado com o e-mail: " + request.getEmail());
                });

        // Adiciona id do usuário e id do tenant aos claims do JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        if (user.getTenant() != null) {
            claims.put("tenantId", user.getTenant().getId());
        }

        // Gera os tokens com os claims adicionais
        String jwtToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("Tokens JWT gerados para o usuário ID: {}, Tenant ID: {}", user.getId(),
                user.getTenant() != null ? user.getTenant().getId() : "N/A");

        // Adiciona informações ao MDC após autenticação bem-sucedida para logs
        // subsequentes
        MDC.put("userId", String.valueOf(user.getId()));
        if (user.getTenant() != null) {
            MDC.put("tenantId", String.valueOf(user.getTenant().getId()));
        }

        return BaseResponse.<AuthResponse>builder()
                // .status("SUCCESS")
                // .message("Autenticação realizada com sucesso.")
                // .timestamp(LocalDateTime.now())
                .data(AuthResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(jwtService.extractExpiration(jwtToken).getTime() / 1000)
                        .build())
                .build();
    }

    /**
     * Renova o access token usando um refresh token.
     * * @param request DTO de requisição de refresh token.
     * 
     * @return DTO de resposta de login com novo access token.
     * @throws ResponseStatusException se o refresh token for inválido ou expirado.
     */
    @Transactional
    public BaseResponse<AuthResponse> refreshToken(RefreshTokenRequestDTO request) {
        log.debug("Iniciando processo de refresh token.");
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        if (userEmail == null) {
            log.warn("Refresh token não contém e-mail de usuário válido.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para o e-mail do refresh token: {}", userEmail);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Usuário não encontrado com o e-mail: " + userEmail);
                });

        if (jwtService.isTokenValid(request.getRefreshToken(), user)) {
            log.info("Refresh token válido para o usuário: {}", userEmail);
            // Ao gerar um novo access token, re-incluímos os claims de userId e tenantId
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            if (user.getTenant() != null) {
                claims.put("tenantId", user.getTenant().getId());
            }
            String accessToken = jwtService.generateToken(claims, user);

            log.info("Novo access token gerado para o usuário ID: {}, Tenant ID: {}", user.getId(),
                    user.getTenant() != null ? user.getTenant().getId() : "N/A");

            // Adiciona informações ao MDC para logs subsequentes
            MDC.put("userId", String.valueOf(user.getId()));
            if (user.getTenant() != null) {
                MDC.put("tenantId", String.valueOf(user.getTenant().getId()));
            }

            return BaseResponse.<AuthResponse>builder()
                    // .status("SUCCESS")
                    // .message("Token atualizado com sucesso.")
                    // .timestamp(LocalDateTime.now())
                    .data(AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.getRefreshToken()) // Mantém o mesmo refresh token
                            .tokenType("Bearer")
                            .expiresIn(jwtService.extractExpiration(accessToken).getTime() / 1000)
                            .build())
                    .build();
        } else {
            log.warn("Refresh token inválido ou expirado para o usuário: {}", userEmail);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado.");
        }
    }
}