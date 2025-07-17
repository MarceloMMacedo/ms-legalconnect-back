package br.com.legalconnect.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.auth.dto.AuthResponse;
import br.com.legalconnect.auth.dto.LoginRequestDTO;
import br.com.legalconnect.auth.dto.RefreshTokenRequestDTO;
import br.com.legalconnect.auth.service.AuthService;
import br.com.legalconnect.common.common_lib.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @class AuthController
 * @brief Controlador REST para endpoints de autenticação.
 *        Gerencia as requisições de login e refresh de tokens JWT.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Endpoint para autenticação de usuário.
     * 
     * @param request DTO de requisição de login.
     * @return Resposta padronizada com tokens JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> authenticate(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Requisição de login recebida para o e-mail: {}", request.getEmail());
        long startTime = System.currentTimeMillis();
        BaseResponse<AuthResponse> response = authService.authenticate(request);
        long endTime = System.currentTimeMillis();
        log.info("Login para o e-mail '{}' processado em {} ms. Status: {}", request.getEmail(), (endTime - startTime),
                "Sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para renovar o access token usando um refresh token.
     * 
     * @param request DTO de requisição de refresh token.
     * @return Resposta padronizada com novo access token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Requisição de refresh token recebida.");
        long startTime = System.currentTimeMillis();
        br.com.legalconnect.common.common_lib.BaseResponse<AuthResponse> response = authService.refreshToken(request);
        long endTime = System.currentTimeMillis();
        log.info("Refresh token processado em {} ms. Status: {}", (endTime - startTime), "Sucesso");
        return ResponseEntity.ok(response);
    }
}