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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaProfissionalRequestDTO {
    private UUID id;

    @NotBlank(message = "O cargo da experiência é obrigatório.")
    @Size(max = 255, message = "O cargo da experiência deve ter no máximo 255 caracteres.")
    private String cargo;

    @NotBlank(message = "O nome da empresa da experiência é obrigatório.")
    @Size(max = 255, message = "O nome da empresa da experiência deve ter no máximo 255 caracteres.")
    private String empresa;

    @NotNull(message = "A data de início da experiência é obrigatória.")
    @PastOrPresent(message = "A data de início da experiência não pode ser uma data futura.")
    private LocalDate dataInicio;

    @PastOrPresent(message = "A data de fim da experiência não pode ser uma data futura.")
    private LocalDate dataFim;

    private String descricao;
}