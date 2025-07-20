package br.com.legalconnect.dto;

import br.com.legalconnect.entity.Profissional.StatusProfissional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class ProfissionalResponseDTO
 * @brief DTO para respostas de Profissional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProfissionalResponseDTO extends PessoaResponseDTO {
    private String numeroOab;
    private StatusProfissional statusProfissional;
    private Boolean usaMarketplace;
    private Boolean fazParteDePlano;
    private EmpresaResponseDTO empresa; // DTO da empresa associada (simplificado)
    private PlanoResponseDTO plano; // DTO do plano de assinatura
}