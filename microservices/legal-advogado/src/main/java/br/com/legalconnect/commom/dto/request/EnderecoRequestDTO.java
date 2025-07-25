package br.com.legalconnect.commom.dto.request; // Assumindo um pacote common.dto.request para entidades comuns

import br.com.legalconnect.commom.model.Endereco.TipoEndereco; // Importar o enum TipoEndereco da entidade Endereco
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de criação ou atualização de Endereço.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoRequestDTO {
    // ID é opcional para atualização (usado em PUT/PATCH)
    private String id; // Pode ser UUID ou String, dependendo da BaseEntity

    @NotBlank(message = "O logradouro é obrigatório.")
    @Size(max = 255, message = "O logradouro deve ter no máximo 255 caracteres.")
    private String logradouro;

    @NotBlank(message = "O número é obrigatório.")
    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres.")
    private String numero;

    @Size(max = 255, message = "O complemento deve ter no máximo 255 caracteres.")
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório.")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(max = 2, message = "O estado deve ter 2 caracteres (UF).")
    private String estado;

    @NotBlank(message = "O CEP é obrigatório.")
    @Size(max = 9, message = "O CEP deve ter no máximo 9 caracteres.") // Com ou sem máscara
    private String cep;

    @Size(max = 50, message = "O país deve ter no máximo 50 caracteres.")
    private String pais; // Valor padrão "Brasil" pode ser setado no serviço

    @NotNull(message = "O tipo de endereço é obrigatório.")
    private TipoEndereco tipoEndereco; // Usando o enum da entidade
}