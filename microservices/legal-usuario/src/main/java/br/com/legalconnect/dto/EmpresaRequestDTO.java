package br.com.legalconnect.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class EmpresaRequestDTO
 * @brief DTO para requisições de criação ou atualização de Empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaRequestDTO {
    private UUID id; // Para atualizações, pode ser necessário o ID

    @NotBlank(message = "O nome fantasia não pode estar em branco.")
    @Size(max = 255, message = "O nome fantasia deve ter no máximo 255 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "A razão social não pode estar em branco.")
    @Size(max = 255, message = "A razão social deve ter no máximo 255 caracteres.")
    private String razaoSocial;

    @NotBlank(message = "O CNPJ não pode estar em branco.")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "Formato de CNPJ inválido. Use XX.XXX.XXX/XXXX-XX.")
    private String cnpj;

    @Email(message = "Formato de email de contato inválido.")
    @Size(max = 255, message = "O email de contato deve ter no máximo 255 caracteres.")
    private String emailContato;

    @Valid // Valida a lista de DTOs aninhados
    private List<EnderecoRequestDTO> enderecos;

    private Set<String> telefones; // Lista de números de telefone como strings
}