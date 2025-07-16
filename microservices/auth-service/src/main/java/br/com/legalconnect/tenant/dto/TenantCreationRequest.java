package br.com.legalconnect.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class TenantCreationRequest
 * @brief DTO para a requisição de criação de um novo Tenant.
 *
 *        Contém os dados necessários para registrar um novo ambiente isolado na
 *        plataforma,
 *        incluindo validações para garantir a integridade dos dados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantCreationRequest {

    @NotBlank(message = "O nome do tenant é obrigatório.")
    @Size(max = 255, message = "O nome do tenant deve ter no máximo 255 caracteres.")
    private String nome;

    @NotBlank(message = "O nome do esquema é obrigatório.")
    @Size(min = 3, max = 63, message = "O nome do esquema deve ter entre 3 e 63 caracteres.")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "O nome do esquema deve conter apenas letras minúsculas, números e underscores.")
    private String schemaName;

    @NotBlank(message = "O e-mail do administrador do tenant é obrigatório.")
    @Size(max = 255, message = "O e-mail do administrador deve ter no máximo 255 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "Formato de e-mail do administrador inválido.")
    private String adminEmail; // E-mail do administrador inicial do tenant
}