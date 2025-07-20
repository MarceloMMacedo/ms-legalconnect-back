package br.com.legalconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class ClienteRequestDTO
 * @brief DTO para requisições de criação ou atualização de Cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClienteRequestDTO extends PessoaRequestDTO {
    private String status;
}