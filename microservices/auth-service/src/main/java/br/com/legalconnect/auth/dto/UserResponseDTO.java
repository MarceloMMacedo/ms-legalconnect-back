package br.com.legalconnect.auth.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class UserResponseDTO
 * @brief DTO para a resposta de um usuário.
 *
 *        Representa os dados de um usuário que são retornados pela API,
 *        incluindo informações básicas, tipo de usuário, status e roles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String telefone;
    private String fotoUrl;
    private String userType; // Representação em String do enum UserType
    private String status; // Representação em String do enum UserStatus
    private Set<String> roles; // Nomes das roles associadas ao usuário
    private UUID tenantId; // ID do tenant ao qual o usuário pertence
    private Instant createdAt;
    private Instant updatedAt;
}
