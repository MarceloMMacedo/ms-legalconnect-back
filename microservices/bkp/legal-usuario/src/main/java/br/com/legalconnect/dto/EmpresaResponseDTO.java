package br.com.legalconnect.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class EmpresaResponseDTO
 * @brief DTO para respostas de Empresa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaResponseDTO {
    private UUID id;
    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private String emailContato;
    private List<EnderecoResponseDTO> enderecos;
    private Set<String> telefones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}