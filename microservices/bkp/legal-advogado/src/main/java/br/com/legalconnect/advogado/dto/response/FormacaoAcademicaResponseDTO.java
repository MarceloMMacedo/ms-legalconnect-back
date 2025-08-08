package br.com.legalconnect.advogado.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Formação Acadêmica.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormacaoAcademicaResponseDTO {
    private UUID id;
    private String curso;
    private String instituicao;
    private LocalDate dataConclusao;
}