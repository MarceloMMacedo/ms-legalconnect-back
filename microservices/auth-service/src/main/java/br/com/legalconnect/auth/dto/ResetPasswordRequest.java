package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class ResetPasswordRequest
 * @brief DTO para a requisição de redefinição de senha.
 *
 *        Contém o token de redefinição e a nova senha do usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "O token de redefinição é obrigatório.")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres.")
    // Adicione regex para complexidade de senha se necessário (ex: maiúscula,
    // minúscula, número, caractere especial)
    private String novaSenha;
}