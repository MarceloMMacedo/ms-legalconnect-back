package br.com.legalconnect.dto;

import java.time.LocalDate;
import java.util.List; // Usar List para Enderecos para manter a ordem se necessário
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class PessoaRequestDTO
 * @brief DTO base para requisições de criação ou atualização de Pessoa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PessoaRequestDTO {
    // Não incluir o ID aqui, pois ele será definido pela entidade base (BaseEntity)
    // e retornado no ResponseDTO.

    @NotNull(message = "Os dados do usuário não podem ser nulos.")
    @Valid // Valida o DTO aninhado
    private UserRequestDTO usuario;

    @NotBlank(message = "O nome completo não pode estar em branco.")
    @Size(max = 255, message = "O nome completo deve ter no máximo 255 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O CPF não pode estar em branco.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "Formato de CPF inválido. Use XXX.XXX.XXX-XX.")
    private String cpf;

    @PastOrPresent(message = "A data de nascimento não pode ser futura.")
    private LocalDate dataNascimento;

    @Valid // Valida a lista de DTOs aninhados
    private List<EnderecoRequestDTO> enderecos; // Usar List para manter a ordem se necessário

    private Set<String> telefones; // Lista de números de telefone como strings
}