package br.com.legalconnect.advogado.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Local de Atuação.
 * Usado para retornar detalhes completos do Local de Atuação.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalAtuacaoResponseDTO {
    private UUID id;
    private String nome;
}