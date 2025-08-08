package br.com.legalconnect.advogado.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Área de Atuação.
 * Usado para retornar detalhes completos da Área de Atuação.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaAtuacaoResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
}