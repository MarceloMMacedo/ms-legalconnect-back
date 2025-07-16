package br.com.legalconnect.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

/**
 * @class RefreshTokenResponseDTO
 * @brief DTO para a representação de um RefreshToken em respostas da API.
 *
 * Inclui o ID do token, o ID do usuário associado, o valor do token,
 * a data de expiração e a data de criação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenResponseDTO {
    private UUID id;
    private UUID userId; // ID do usuário associado ao Refresh Token
    private String token;
    private Instant expiresAt;
    private Instant createdAt;
}