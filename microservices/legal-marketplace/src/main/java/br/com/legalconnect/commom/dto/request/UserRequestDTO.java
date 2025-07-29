package br.com.legalconnect.commom.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de criação ou atualização de um usuário.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    // ID é opcional para atualização (usado em PUT/put)
    private UUID id;

    @NotBlank(message = "O e-mail do usuário é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail do usuário deve ter no máximo 255 caracteres.")
    private String email;

    @Size(max = 255, message = "O nome completo do usuário deve ter no máximo 255 caracteres.")
    private String nomeCompleto; // Campo duplicado com Pessoa, se Pessoa for a principal

    @Size(max = 20, message = "O telefone do usuário deve ter no máximo 20 caracteres.")
    private String telefone; // Campo duplicado com Pessoa, se Pessoa for a principal

    private String fotoUrl; // URL para a foto de perfil
}