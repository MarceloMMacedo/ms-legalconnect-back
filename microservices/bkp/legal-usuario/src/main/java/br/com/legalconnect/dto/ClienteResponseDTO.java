package br.com.legalconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class ClienteResponseDTO
 * @brief DTO para respostas de Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClienteResponseDTO extends PessoaResponseDTO {
    private String status;
    private String tipo;
}