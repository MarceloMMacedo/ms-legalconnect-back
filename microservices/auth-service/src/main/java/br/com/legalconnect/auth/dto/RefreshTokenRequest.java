package br.com.legalconnect.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

/**
 * @class RefreshTokenRequest
 * @brief DTO para requisição de refresh de token.
 *
 * Utilizado para enviar o Refresh Token para obter um novo Access Token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    @NotBlank(message = "O refresh token é obrigatório.")
    private String refreshToken;
}