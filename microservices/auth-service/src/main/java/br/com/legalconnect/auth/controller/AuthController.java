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
import br.com.legalconnect.auth.dto.RecoverPasswordRequest;
import br.com.legalconnect.auth.dto.RefreshTokenRequestDTO;
import br.com.legalconnect.auth.dto.ResetPasswordRequest;
import br.com.legalconnect.auth.dto.UserRegistrationRequest;
import br.com.legalconnect.auth.dto.UserResponseDTO;
import br.com.legalconnect.auth.service.AuthService;
import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.enums.StatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * "/api/v1/usuarios/redefinir-senha/solicitar",
 * "/api/v1/usuarios/redefinir-senha/confirmar",
 * 
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
                log.info("Login para o e-mail '{}' processado em {} ms. Status: {}", request.getEmail(),
                                (endTime - startTime),
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
        public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(
                        @Valid @RequestBody RefreshTokenRequestDTO request) {
                log.info("Requisição de refresh token recebida.");
                long startTime = System.currentTimeMillis();
                BaseResponse<AuthResponse> response = authService.refreshToken(request);
                long endTime = System.currentTimeMillis();
                log.info("Refresh token processado em {} ms. Status: {}", (endTime - startTime), "Sucesso");
                return ResponseEntity.ok(response);
        }

        /**
         * Endpoint para registro de um novo cliente.
         * 
         * @param request DTO de requisição de registro de usuário.
         * @return Resposta padronizada com DTO do usuário registrado.
         */
        @PostMapping("/clientes/registrar")
        public ResponseEntity<BaseResponse<UserResponseDTO>> registerClient(
                        @Valid @RequestBody UserRegistrationRequest request) {
                log.info("Requisição de registro de cliente recebida para o e-mail: {}", request.getEmail());
                long startTime = System.currentTimeMillis();
                UserResponseDTO registeredUser = authService.registerClient(request);
                long endTime = System.currentTimeMillis();
                log.info("Registro de cliente para o e-mail '{}' processado em {} ms. Status: Sucesso",
                                request.getEmail(),
                                (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<UserResponseDTO>builder()
                                .data(registeredUser)
                                .message("Cliente registrado com sucesso!")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }

        /**
         * Endpoint para registro de um novo advogado.
         * 
         * @param request DTO de requisição de registro de usuário.
         * @return Resposta padronizada com DTO do usuário registrado.
         */
        @PostMapping("/advogados/registrar")
        public ResponseEntity<BaseResponse<UserResponseDTO>> registerAdvogado(
                        @Valid @RequestBody UserRegistrationRequest request) {
                log.info("Requisição de registro de advogado recebida para o e-mail: {}", request.getEmail());
                long startTime = System.currentTimeMillis();
                UserResponseDTO registeredUser = authService.registerAdvogado(request);
                long endTime = System.currentTimeMillis();
                log.info("Registro de advogado para o e-mail '{}' processado em {} ms. Status: Sucesso",
                                request.getEmail(),
                                (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<UserResponseDTO>builder()
                                .data(registeredUser)
                                .message("Advogado pré-registrado com sucesso! Aguardando aprovação.")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }

        /**
         * Endpoint para registro de um novo sócio.
         * 
         * @param request DTO de requisição de registro de usuário.
         * @return Resposta padronizada com DTO do usuário registrado.
         */
        @PostMapping("/socios/registrar")
        public ResponseEntity<BaseResponse<UserResponseDTO>> registerSocio(
                        @Valid @RequestBody UserRegistrationRequest request) {
                log.info("Requisição de registro de sócio recebida para o e-mail: {}", request.getEmail());
                long startTime = System.currentTimeMillis();
                UserResponseDTO registeredUser = authService.registerSocio(request);
                long endTime = System.currentTimeMillis();
                log.info("Registro de sócio para o e-mail '{}' processado em {} ms. Status: Sucesso",
                                request.getEmail(),
                                (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<UserResponseDTO>builder()
                                .data(registeredUser)
                                .message("Sócio registrado com sucesso! Aguardando aprovação.")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }

        /**
         * Endpoint para registro de um novo administrador da plataforma.
         * 
         * @param request DTO de requisição de registro de usuário.
         * @return Resposta padronizada com DTO do usuário registrado.
         */
        @PostMapping("/administradores/registrar")
        public ResponseEntity<BaseResponse<UserResponseDTO>> registerAdmin(
                        @Valid @RequestBody UserRegistrationRequest request) {
                log.info("Requisição de registro de administrador recebida para o e-mail: {}", request.getEmail());
                long startTime = System.currentTimeMillis();
                UserResponseDTO registeredUser = authService.registerAdmin(request);
                long endTime = System.currentTimeMillis();
                log.info("Registro de administrador para o e-mail '{}' processado em {} ms. Status: Sucesso",
                                request.getEmail(), (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<UserResponseDTO>builder()
                                .data(registeredUser)
                                .message("Administrador registrado com sucesso! Aguardando aprovação.")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }

        /**
         * Endpoint para iniciar o processo de recuperação de senha.
         * 
         * @param request DTO contendo o e-mail do usuário.
         * @return Resposta padronizada de sucesso.
         */
        @PostMapping("/senhas/recuperar")
        public ResponseEntity<BaseResponse<Void>> recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
                log.info("Requisição de recuperação de senha recebida para o e-mail: {}", request.getEmail());
                long startTime = System.currentTimeMillis();
                authService.recoverPassword(request.getEmail());
                long endTime = System.currentTimeMillis();
                log.info("Processo de recuperação de senha para o e-mail '{}' processado em {} ms. Status: Sucesso",
                                request.getEmail(), (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .message("Se o e-mail estiver cadastrado, um link de redefinição de senha foi enviado.")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }

        /**
         * Endpoint para redefinir a senha usando um token.
         * 
         * @param request DTO contendo o token e a nova senha.
         * @return Resposta padronizada de sucesso.
         */
        @PostMapping("/senhas/redefinir")
        public ResponseEntity<BaseResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
                log.info("Requisição de redefinição de senha recebida com token.");
                long startTime = System.currentTimeMillis();
                authService.resetPassword(request.getToken(), request.getNovaSenha());
                long endTime = System.currentTimeMillis();
                log.info("Redefinição de senha processada em {} ms. Status: Sucesso", (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .message("Senha redefinida com sucesso!")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }
}