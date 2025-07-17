package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class RefreshTokenRequestDTO
 * @brief DTO para a requisição de refresh de token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {
    @NotBlank(message = "O refresh token é obrigatório.")
    private String refreshToken;
}