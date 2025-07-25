package br.com.legalconnect.commom.model;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Endereco
 * @brief Entidade que representa um endereço detalhado.
 *        Pode ser associada a Pessoas ou Empresas.
 *        Mapeada para a tabela 'tb_endereco'.
 */
@Entity
@Table(name = "tb_endereco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Endereco extends BaseEntity {

    @Column(name = "logradouro", nullable = false, length = 255)
    private String logradouro; // Nome da rua, avenida, etc.

    @Column(name = "numero", nullable = false, length = 20)
    private String numero; // Número do imóvel

    @Column(name = "complemento", length = 255)
    private String complemento; // Complemento (ex: apto, sala, bloco)

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro; // Bairro

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade; // Cidade

    @Column(name = "estado", nullable = false, length = 2)
    private String estado; // Estado (UF)

    @Column(name = "cep", nullable = false, length = 9) // CEP com máscara
    private String cep; // Código de Endereçamento Postal

    @Column(name = "pais", nullable = false, length = 50)
    private String pais = "Brasil"; // País (valor padrão "Brasil")

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_endereco", nullable = false, length = 50)
    private TipoEndereco tipoEndereco; // Tipo de endereço (ex: RESIDENCIAL, COMERCIAL, ESCRITORIO)

    /**
     * @brief Relacionamento muitos-para-um com a entidade Pessoa (opcional).
     *        Um endereço pode pertencer a uma pessoa.
     *        Somente um dos campos (pessoa ou empresa) deve ser preenchido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    /**
     * @brief Relacionamento muitos-para-um com a entidade Empresa (opcional).
     *        Um endereço pode pertencer a uma empresa.
     *        Somente um dos campos (pessoa ou empresa) deve ser preenchido.
     */
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "empresa_id")
    // private Empresa empresa;

    /**
     * @enum TipoEndereco
     * @brief Enumeração para representar o tipo de endereço.
     */
    public enum TipoEndereco {
        RESIDENCIAL,
        COMERCIAL,
        ESCRITORIO,
        COBRANCA,
        ENTREGA,
        OUTRO
    }
}