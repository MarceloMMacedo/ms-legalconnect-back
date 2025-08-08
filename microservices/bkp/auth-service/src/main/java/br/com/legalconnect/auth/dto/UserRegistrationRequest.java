package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class UserRegistrationRequest
 * @brief DTO para a requisição de registro de um novo usuário.
 *
 *        Contém os dados necessários para registrar um novo usuário na
 *        plataforma,
 *        incluindo validações para garantir a integridade dos dados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(max = 255, message = "O nome completo deve ter no máximo 255 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    private String email;

    // @NotBlank(message = "O CPF é obrigatório.")
    // @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "Formato de
    // CPF inválido. Use XXX.XXX.XXX-XX")
    // private String cpf;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    private String telefone;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    // Adicione regex para complexidade de senha se necessário (ex: maiúscula,
    // minúscula, número, caractere especial)
    private String senha;

    // @NotBlank(message = "O tipo de usuário é obrigatório.")
    // private String userType; // Adicionado para especificar o tipo de usuário
    // (CLIENTE, ADVOGADO, SOCIO,
    // // PLATAFORMA_ADMIN)
}
