//
// DTO específico para a resposta de um patrocínio de escritório.
//
package br.com.legalconnect.patrocinio.application.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PatrocinioEscritorioResponseDTO extends PatrocinioResponseDTO {
    private String nome;
    private String slogan;
    private String logoUrl;
}