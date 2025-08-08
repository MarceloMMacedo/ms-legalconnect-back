package br.com.legalconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class AdministradorRequestDTO
 * @brief DTO para requisições de criação ou atualização de Administrador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdministradorRequestDTO extends PessoaRequestDTO {
    private String status;
}