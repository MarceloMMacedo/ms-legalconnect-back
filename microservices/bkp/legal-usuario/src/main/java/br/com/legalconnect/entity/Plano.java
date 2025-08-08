package br.com.legalconnect.entity;

import java.math.BigDecimal;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Plano
 * @brief Entidade que representa um plano de assinatura oferecido na
 *        plataforma.
 *        Esta classe é um placeholder e deve ser definida em seu próprio
 *        microsserviço (marketplace).
 *        Mapeada para a tabela 'tb_plano'.
 */
@Entity
@Table(name = "tb_plano")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Plano extends BaseEntity {

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome; // Nome do plano (ex: Free, Premium)

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao; // Descrição detalhada do plano

    @Column(name = "preco_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoMensal; // Preço mensal do plano

    @Column(name = "preco_anual", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAnual; // Preço anual do plano

    @Column(name = "limite_servicos_agendaveis")
    private Integer limiteServicosAgendaveis; // Limite de serviços que podem ser agendados

    @Enumerated(EnumType.STRING)
    @Column(name = "visibilidade_destaque", nullable = false, length = 50)
    private VisibilidadeDestaque visibilidadeDestaque; // Nível de destaque no marketplace

    @Column(name = "acesso_relatorios_avancados", nullable = false)
    private Boolean acessoRelatoriosAvancados; // Indica se o plano dá acesso a relatórios avançados

    @Column(name = "permite_pedidos_orcamento", nullable = false)
    private Boolean permitePedidosOrcamento; // Indica se o plano permite pedidos de orçamento

    @Column(name = "periodo_teste_dias")
    private Integer periodoTesteDias; // Período de teste gratuito em dias

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault; // Indica se é o plano padrão para novos cadastros

    /**
     * @enum VisibilidadeDestaque
     * @brief Enumeração para representar o nível de visibilidade/destaque de um
     *        plano no marketplace.
     */
    public enum VisibilidadeDestaque {
        PADRAO,
        PREMIUM,
        DESTAQUE_MAXIMO
    }
}