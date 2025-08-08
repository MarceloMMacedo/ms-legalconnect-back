package br.com.legalconnect.commom.dto.response;

import java.util.UUID;

import br.com.legalconnect.commom.model.User.UserStatus; // Importar o enum UserStatus da entidade User
import br.com.legalconnect.commom.model.User.UserType; // Importar o enum UserType da entidade User
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de um usuário.
 * Não inclui a senha hash por questões de segurança.
 * 
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
    private UserType userType; // Tipo de usuário (CLIENTE, ADVOGADO, etc.)
    private UserStatus userStatus; // Status da conta do usuário
}