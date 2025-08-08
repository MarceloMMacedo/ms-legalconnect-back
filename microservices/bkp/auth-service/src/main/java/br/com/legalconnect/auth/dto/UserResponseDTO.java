package br.com.legalconnect.auth.dto;

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
    // private UUID id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String telefone;
    private String fotoUrl;
    private String status; // Representação em String do enum UserStatus

}
