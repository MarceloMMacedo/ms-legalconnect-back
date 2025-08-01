//
// DTO específico para a resposta de um patrocínio de evento.
//
package br.com.legalconnect.patrocinio.application.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PatrocinioEventoResponseDTO extends PatrocinioResponseDTO {
    private String titulo;
    private LocalDateTime dataEvento;
    private String imagemUrl;
}