package br.com.legalconnect.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * @class AuthResponse
 * @brief DTO para resposta de login (access_token, refresh_token).
 *
 * Contém os tokens JWT e Refresh Token retornados após um login bem-sucedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}