package br.com.legalconnect.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
// Importações do Swagger/SpringDoc OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Gerencia o fluxo de login, refresh e logout de usuários na aplicação LegalConnect.") // Anotação
                                                                                                                                // para
                                                                                                                                // o
                                                                                                                                // nome
                                                                                                                                // do
                                                                                                                                // grupo
                                                                                                                                // no
                                                                                                                                // Swagger
                                                                                                                                // UI
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
        @Operation(summary = "Realiza o login de um usuário", // Resumo da operação
                        description = "Autentica o usuário com e-mail e senha e retorna tokens de acesso e refresh (RF049).", // Descrição
                                                                                                                              // detalhada
                        tags = { "Autenticação" } // Opcional, mas útil para agrupar
        )
        @ApiResponses(value = { // Definição das possíveis respostas da API
                        @ApiResponse(responseCode = "200", description = "Login bem-sucedido. Retorna o access token e refresh token.", content = @Content(schema = @Schema(implementation = AuthResponse.class)) // Define
                                                                                                                                                                                                                  // o
                                                                                                                                                                                                                  // schema
                                                                                                                                                                                                                  // da
                                                                                                                                                                                                                  // resposta
                                                                                                                                                                                                                  // de
                                                                                                                                                                                                                  // sucesso
                        ),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida (e.g., e-mail ou senha ausentes).", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Credenciais inválidas (e.g., e-mail ou senha incorretos).", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        })
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
        @Operation(summary = "Renova o Access Token com um Refresh Token", description = "Usa um refresh token válido para obter um novo access token e refresh token (RF051).", tags = {
                        "Autenticação" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso.", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida (e.g., refresh token ausente ou mal formatado).", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado.", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        })
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
        @Operation(summary = "Realiza o logout de um usuário", description = "Invalida o refresh token fornecido, encerrando a sessão do usuário (RF052).", tags = {
                        "Autenticação" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso.", content = @Content(schema = @Schema(implementation = SuccessResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida (e.g., refresh token ausente).", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Refresh token inválido.", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        })
        @PostMapping("/logout")
        public ResponseEntity<BaseResponse<SuccessResponseDTO>> logout(
                        @Valid @RequestBody RefreshTokenRequest request) {
                log.info("Requisição de logout recebida para o refresh token: {}", request.getRefreshToken());
                // 1. Chama o serviço de autenticação para invalidar o refresh token
                authService.logout(request.getRefreshToken());
                // 2. Retorna uma resposta de sucesso
                log.info("Logout realizado com sucesso para o refresh token.");
                return ResponseEntity.ok(BaseResponse.<SuccessResponseDTO>builder()
                                .data((SuccessResponseDTO) SuccessResponseDTO.builder()
                                                .message("Logout realizado com sucesso.")
                                                .build())
                                .build());
        }

        /**
         * @brief Endpoint para login/cadastro via autenticação social (Google,
         *        LinkedIn) (RF054).
         * @param request DTO contendo o provedor e o token social.
         * @return ResponseEntity com BaseResponse contendo AuthResponse.
         *         * @PostMapping("/social-login")
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