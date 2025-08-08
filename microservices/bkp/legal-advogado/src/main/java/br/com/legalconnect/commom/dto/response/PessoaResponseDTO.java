package br.com.legalconnect.commom.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de uma Pessoa.
 * Inclui informações de User e uma lista de Endereços.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaResponseDTO {
    private UUID id;

    private UserResponseDTO usuario;

    private String nomeCompleto;

    private String cpf;

    private LocalDate dataNascimento;

    private List<String> telefones;

    private List<EnderecoResponseDTO> enderecos;
}