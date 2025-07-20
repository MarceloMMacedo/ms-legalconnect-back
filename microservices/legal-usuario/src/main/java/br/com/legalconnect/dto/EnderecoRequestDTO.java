package br.com.legalconnect.dto;

import java.util.UUID;

import br.com.legalconnect.entity.Endereco.TipoEndereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class EnderecoRequestDTO
 * @brief DTO para requisições de criação ou atualização de Endereco.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoRequestDTO {
    private UUID id; // Para atualizações, pode ser necessário o ID

    @NotBlank(message = "O logradouro não pode estar em branco.")
    @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres.")
    private String logradouro;

    @NotBlank(message = "O número não pode estar em branco.")
    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres.")
    private String numero;

    @Size(max = 255, message = "O complemento deve ter no máximo 255 caracteres.")
    private String complemento;

    @NotBlank(message = "O bairro não pode estar em branco.")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
    private String bairro;

    @NotBlank(message = "A cidade não pode estar em branco.")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @NotBlank(message = "O estado não pode estar em branco.")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 caracteres (UF).")
    private String estado;

    @NotBlank(message = "O CEP não pode estar em branco.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "Formato de CEP inválido. Use XXXXX-XXX.")
    private String cep;

    @NotBlank(message = "O país não pode estar em branco.")
    @Size(max = 50, message = "O país deve ter no máximo 50 caracteres.")
    private String pais;

    @NotNull(message = "O tipo de endereço não pode ser nulo.")
    private TipoEndereco tipoEndereco;
}