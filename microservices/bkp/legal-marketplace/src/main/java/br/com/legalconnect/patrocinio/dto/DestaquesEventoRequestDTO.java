//
// DTO específico para um patrocínio de evento.
//
package br.com.legalconnect.patrocinio.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DestaquesEventoRequestDTO extends DestaquesRequestDTO {
    @NotBlank(message = "O título do evento é obrigatório.")
    private String titulo;
    @NotNull(message = "A data do evento é obrigatória.")
    private LocalDateTime dataEvento;
    @NotBlank(message = "A URL da imagem é obrigatória.")
    private String imagemUrl;
}