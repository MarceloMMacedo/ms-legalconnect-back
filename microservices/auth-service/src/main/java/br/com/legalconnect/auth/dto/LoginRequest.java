package br.com.legalconnect.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;

/**
 * @class LoginRequest
 * @brief DTO para dados de requisição de login (e-mail, senha).
 *
 * Utilizado para receber as credenciais do usuário durante o processo de login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;
}