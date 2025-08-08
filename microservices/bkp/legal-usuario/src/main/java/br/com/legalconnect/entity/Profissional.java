package br.com.legalconnect.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Profissional
 * @brief Entidade que representa um profissional do direito.
 *        Estende a entidade Pessoa e adiciona campos específicos de um
 *        profissional.
 *        Mapeado para a tabela 'tb_profissional' que se junta a 'tb_pessoa'
 *        pela chave primária.
 */
@Entity
@Table(name = "tb_profissional")
@PrimaryKeyJoinColumn(name = "id")
@Getter

@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Profissional extends Pessoa {

    @Column(name = "numero_oab", nullable = false, unique = true, length = 50)
    private String numeroOab; // Número de registro na OAB, único por profissional

    @Enumerated(EnumType.STRING)
    @Column(name = "status_profissional", nullable = false, length = 50)
    private StatusProfissional statusProfissional; // Status específico do profissional (ex: ATIVO, LICENCIADO)

    @Column(name = "usa_marketplace", nullable = false)
    private Boolean usaMarketplace = false; // Indica se o profissional deseja aparecer no marketplace

    @Column(name = "faz_parte_de_plano", nullable = false)
    private Boolean fazParteDePlano = false; // Indica se o profissional está associado a um plano pago

    /**
     * @brief Relacionamento muitos-para-um com a entidade Empresa (opcional).
     *        Um profissional pode pertencer a uma empresa.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id") // Coluna de chave estrangeira para a empresa
    private Empresa empresa; // A empresa à qual o profissional está associado (se houver)

    /**
     * @brief Relacionamento muitos-para-um com a entidade Plano.
     *        Um profissional está associado a um plano de assinatura.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false) // Coluna de chave estrangeira para o plano
    private Plano plano; // O plano de assinatura do advogado
    @ManyToMany(fetch = FetchType.EAGER) // Relacionamento muitos-para-muitos com Role
    @JoinTable(name = "tb_user_profissionals_role", // Tabela de junção
            joinColumns = @JoinColumn(name = "profissional_profissionals_id"), // Coluna que referencia User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Coluna que referencia Role
    )
    private Set<RoleProfissional> roleProfissionals = new HashSet<>(); // Papéis/perfil de acesso do usuário

    /**
     * @enum StatusProfissional
     * @brief Enumeração para representar o status específico de um Profissional.
     */
    public enum StatusProfissional {
        ATIVO, // Profissional ativo e em dia
        LICENCIADO, // Profissional com licença temporariamente suspensa
        SUSPENSO, // Profissional suspenso (ex: por infração)
        EM_ANALISE // Profissional aguardando aprovação/verificação
    }
}