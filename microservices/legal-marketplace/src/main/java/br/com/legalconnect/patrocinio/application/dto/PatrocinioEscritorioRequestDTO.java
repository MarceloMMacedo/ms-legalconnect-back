//
// DTO específico para um patrocínio de escritório.
//
package br.com.legalconnect.patrocinio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PatrocinioEscritorioRequestDTO extends PatrocinioRequestDTO {
    @NotBlank(message = "O nome do escritório é obrigatório.")
    private String nome;
    @NotBlank(message = "O slogan do escritório é obrigatório.")
    private String slogan;
    @Size(max = 255, message = "A URL da logo deve ter no máximo 255 caracteres.")
    private String logoUrl; // Opcional, se não houver, pode ser usado um placeholder
}