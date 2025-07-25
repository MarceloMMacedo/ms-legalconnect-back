package br.com.legalconnect.advogado.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Idioma.
 * Usado para retornar detalhes completos do Idioma.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdiomaResponseDTO {
    private UUID id;
    private String nome;
    private String codigo;
    private String nivel;
}