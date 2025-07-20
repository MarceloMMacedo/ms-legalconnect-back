package br.com.legalconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class UserRequestDTO
 * @brief DTO para requisições de criação ou atualização de User.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank(message = "O email não pode estar em branco.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    private String password;

    // O papel (Role) pode ser definido aqui ou inferido pelo serviço de
    // autenticação/usuário
    // dependendo da lógica de negócio. Para simplificar, pode ser incluído se for
    // um input direto.
    // private String role;
}