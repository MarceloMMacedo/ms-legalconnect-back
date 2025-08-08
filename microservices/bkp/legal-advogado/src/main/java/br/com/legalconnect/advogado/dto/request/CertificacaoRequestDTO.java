package br.com.legalconnect.advogado.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de Certificação.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificacaoRequestDTO {

    private UUID id; // Para identificação em operações de atualização/deleção

    @NotBlank(message = "O nome da certificação é obrigatório.")
    @Size(max = 255, message = "O nome da certificação deve ter no máximo 255 caracteres.")
    private String nome;

    @Size(max = 255, message = "A instituição da certificação deve ter no máximo 255 caracteres.")
    private String instituicao;

    @NotNull(message = "A data de conclusão da certificação é obrigatória.")
    @PastOrPresent(message = "A data de conclusão da certificação não pode ser uma data futura.")
    private LocalDate dataConclusao;
}