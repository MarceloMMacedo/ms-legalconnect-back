package br.com.legalconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class AdministradorResponseDTO
 * @brief DTO para respostas de Administrador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AdministradorResponseDTO extends PessoaResponseDTO {
    private String status;
}