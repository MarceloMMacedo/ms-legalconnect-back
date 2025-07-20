package br.com.legalconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.legalconnect.entity.Plano.VisibilidadeDestaque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class PlanoResponseDTO
 * @brief DTO para respostas de Plano.
 *        Esta classe é um DTO simplificado para ser usado em outros
 *        microsserviços.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanoResponseDTO {
    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal precoMensal;
    private BigDecimal precoAnual;
    private Integer limiteServicosAgendaveis;
    private VisibilidadeDestaque visibilidadeDestaque;
    private Boolean acessoRelatoriosAvancados;
    private Boolean permitePedidosOrcamento;
    private Integer periodoTesteDias;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}