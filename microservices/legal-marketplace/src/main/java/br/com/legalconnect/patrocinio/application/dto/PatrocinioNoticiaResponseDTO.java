//
// DTO específico para a resposta de um patrocínio de notícia.
//
package br.com.legalconnect.patrocinio.application.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PatrocinioNoticiaResponseDTO extends PatrocinioResponseDTO {
    private String titulo;
    private String imagemUrl;
    private LocalDateTime dataPublicacao;
}