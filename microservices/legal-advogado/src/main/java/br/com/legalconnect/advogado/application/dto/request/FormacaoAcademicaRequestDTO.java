package br.com.legalconnect.advogado.application.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de Formação Acadêmica.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormacaoAcademicaRequestDTO {

    private UUID id; // Para identificação em operações de atualização/deleção

    @NotBlank(message = "O nome do curso é obrigatório.")
    @Size(max = 255, message = "O nome do curso deve ter no máximo 255 caracteres.")
    private String curso;

    @NotBlank(message = "O nome da instituição é obrigatório.")
    @Size(max = 255, message = "O nome da instituição deve ter no máximo 255 caracteres.")
    private String instituicao;

    @NotNull(message = "A data de conclusão da formação é obrigatória.")
    @PastOrPresent(message = "A data de conclusão da formação não pode ser uma data futura.")
    private LocalDate dataConclusao;
}