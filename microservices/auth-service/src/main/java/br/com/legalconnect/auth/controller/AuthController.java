package br.com.legalconnect.auth.controller;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.auth.dto.AuthResponse;
import br.com.legalconnect.auth.dto.LoginRequest;
import br.com.legalconnect.auth.dto.RefreshTokenRequest;
import br.com.legalconnect.auth.service.AuthService;
import br.com.legalconnect.common.common_lib.BaseResponse;
import br.com.legalconnect.common.dto.SuccessResponseDTO;
import jakarta.validation.Valid;

/**
 * @class AuthController
 * @brief Controlador REST para endpoints de autenticação e gerenciamento de
 *        sessão.
 *
 *        Gerencia requisições de login (RF049), refresh de token (RF051),
 *        logout (RF052) e autenticação social (RF054).
 */
@RestController
@RequestMapping("/api/v1/public/auth") // Endpoints públicos para autenticação
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class); // Instância do Logger

    @Autowired
    private AuthService authService; /// < Serviço de autenticação.

    /**
     * @brief Endpoint para login de usuários (RF049).
     * @param request DTO contendo e-mail e senha.
     * @return ResponseEntity com BaseResponse contendo AuthResponse (access token e
     *         refresh token).
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requisição de login recebida para o e-mail: {}", request.getEmail());
        // 1. Chama o serviço de autenticação para processar o login
        AuthResponse authResponse = authService.login(request);
        // 2. Retorna uma resposta de sucesso com os tokens
        log.info("Login bem-sucedido para o e-mail: {}", request.getEmail());
        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .data(authResponse)
                .build());
    }

    /**
     * @brief Endpoint para renovar o Access Token usando um Refresh Token (RF051).
     * @param request DTO contendo o Refresh Token.
     * @return ResponseEntity com BaseResponse contendo AuthResponse (novo access
     *         token e refresh token).
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<AuthResponse>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        log.info("Requisição de refresh token recebida.");
        // 1. Chama o serviço de autenticação para renovar o token
        AuthResponse authResponse = authService.refreshAccessToken(request);
        // 2. Retorna uma resposta de sucesso com os novos tokens
        log.info("Refresh token processado com sucesso.");
        return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
                .data(authResponse)
                .build());
    }

    /**
     * @brief Endpoint para logout de usuários, invalidando o Refresh Token (RF052).
     * @param request DTO contendo o Refresh Token a ser invalidado.
     * @return ResponseEntity com BaseResponse de sucesso.
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<SuccessResponseDTO>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Requisição de logout recebida para o refresh token: {}", request.getRefreshToken());
        // 1. Chama o serviço de autenticação para invalidar o refresh token
        authService.logout(request.getRefreshToken());
        // 2. Retorna uma resposta de sucesso
        log.info("Logout realizado com sucesso para o refresh token.");
        return ResponseEntity.ok(BaseResponse.<SuccessResponseDTO>builder()
                .data((SuccessResponseDTO) SuccessResponseDTO.builder().message("Logout realizado com sucesso.")
                        .build())
                .build());
    }

    /**
     * @brief Endpoint para login/cadastro via autenticação social (Google,
     *        LinkedIn) (RF054).
     * @param request DTO contendo o provedor e o token social.
     * @return ResponseEntity com BaseResponse contendo AuthResponse.
     * 
     *         @PostMapping("/social-login")
     *         public ResponseEntity<BaseResponse<AuthResponse>>
     *         socialLogin(@Valid @RequestBody SocialLoginRequest request) {
     *         log.info("Requisição de login social recebida para o provedor: {}",
     *         request.getProvider());
     *         // 1. Chama o serviço de autenticação para processar o login social
     *         AuthResponse authResponse = authService.socialLogin(request);
     *         // 2. Retorna uma resposta de sucesso com os tokens
     *         log.info("Login social bem-sucedido para o provedor: {}",
     *         request.getProvider());
     *         return ResponseEntity.ok(BaseResponse.<AuthResponse>builder()
     *         .data(authResponse)
     *         .build());
     *         }
     */
}