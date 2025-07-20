package br.com.legalconnect.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class PessoaResponseDTO
 * @brief DTO base para respostas de Pessoa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PessoaResponseDTO {
    private UUID id;
    private UserResponseDTO usuario;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private List<EnderecoResponseDTO> enderecos;
    private Set<String> telefones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}