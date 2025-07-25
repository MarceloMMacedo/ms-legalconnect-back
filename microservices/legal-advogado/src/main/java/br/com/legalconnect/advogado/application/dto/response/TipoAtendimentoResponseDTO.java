package br.com.legalconnect.advogado.application.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Tipo de Atendimento.
 * Usado para retornar detalhes completos do Tipo de Atendimento.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoAtendimentoResponseDTO {
    private UUID id;
    private String nome;
}