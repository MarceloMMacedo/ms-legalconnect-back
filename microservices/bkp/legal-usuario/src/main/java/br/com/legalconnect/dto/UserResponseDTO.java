package br.com.legalconnect.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class UserResponseDTO
 * @brief DTO para respostas de User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // private String role; // Incluir se o papel for relevante na resposta
}