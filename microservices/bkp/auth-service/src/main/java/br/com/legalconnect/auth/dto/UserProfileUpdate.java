package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class UserProfileUpdate
 * @brief DTO para a requisição de atualização de perfil de usuário.
 *
 *        Contém os campos que podem ser atualizados no perfil de um usuário,
 *        incluindo validações para garantir a integridade dos dados.
 *        Os campos são opcionais, permitindo atualizações parciais.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdate {

    @Size(max = 255, message = "O nome completo deve ter no máximo 255 caracteres.")
    private String nomeCompleto;

    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    private String email;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    private String telefone;

    @Size(max = 255, message = "A URL da foto deve ter no máximo 255 caracteres.")
    private String fotoUrl;

    // Campos para alteração de senha (opcionais, mas devem ser fornecidos juntos)
    private String senhaAtual;
    @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres.")
    private String novaSenha;
    // Adicione regex para complexidade de senha se necessário
}