//
// DTO específico para um patrocínio de notícia.
//
package br.com.legalconnect.patrocinio.application.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PatrocinioNoticiaRequestDTO extends PatrocinioRequestDTO {
    @NotBlank(message = "O título da notícia é obrigatório.")
    private String titulo;
    @NotBlank(message = "A URL da imagem é obrigatória.")
    private String imagemUrl;
    @NotNull(message = "A data de publicação da notícia é obrigatória.")
    private LocalDateTime dataPublicacao;
}