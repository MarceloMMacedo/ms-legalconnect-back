package br.com.legalconnect.commom.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO para requisição de criação ou atualização de uma Pessoa.
 * Inclui campos de User e uma lista de Endereços.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PessoaRequestDTO {
    // ID é opcional para atualização (usado em PUT/put para identificar a Pessoa)
    private UUID id;

    @Valid
    @NotNull(message = "As informações do usuário são obrigatórias.")
    private UserRequestDTO usuario; // Informações de login e contato do usuário

    @NotBlank(message = "O nome completo da pessoa é obrigatório.")
    @Size(max = 255, message = "O nome completo da pessoa deve ter no máximo 255 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF da pessoa é obrigatório.")
    @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres (com ou sem formatação).")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", message = "Formato de CPF inválido. Use '000.000.000-00' ou apenas dígitos.")
    private String cpf;

    @NotNull(message = "A data de nascimento da pessoa é obrigatória.")
    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    private LocalDate dataNascimento;

    @Size(max = 10, message = "A lista de telefones não pode exceder 10 itens.")
    private List<String> telefones;

    @Valid
    private List<EnderecoRequestDTO> enderecos; // Lista de endereços associados à pessoa
}