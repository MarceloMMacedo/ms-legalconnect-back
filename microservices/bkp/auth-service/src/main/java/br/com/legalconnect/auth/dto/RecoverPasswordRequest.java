package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class RecoverPasswordRequest
 * @brief DTO para a requisição de recuperação de senha.
 *
 *        Contém o e-mail do usuário que solicitou a recuperação de senha.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoverPasswordRequest {
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;
}