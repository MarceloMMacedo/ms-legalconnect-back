package br.com.legalconnect.entity;

import java.util.HashSet;
import java.util.Set;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Empresa
 * @brief Entidade que representa uma empresa que pode contratar planos na
 *        plataforma.
 *        Esta entidade não estende Pessoa, pois representa uma pessoa jurídica.
 *        Mapeada para a tabela 'tb_empresa'.
 */
@Entity
@Table(name = "tb_empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Empresa extends BaseEntity {

    @Column(name = "nome_fantasia", nullable = false, length = 255)
    private String nomeFantasia; // Nome fantasia da empresa

    @Column(name = "razao_social", nullable = false, length = 255)
    private String razaoSocial; // Razão social da empresa

    @Column(name = "cnpj", nullable = false, unique = true, length = 18) // CNPJ com máscara
    private String cnpj; // Número do Cadastro Nacional de Pessoas Jurídicas (CNPJ), único por empresa

    @Column(name = "email_contato", length = 255)
    private String emailContato; // E-mail de contato da empresa

    /**
     * @brief Relacionamento um-para-muitos com a entidade Endereco.
     *        Uma empresa pode ter múltiplos endereços (sede, filiais, etc.).
     *        CascadeType.ALL garante que operações nos Enderecos se propaguem para
     *        Empresa.
     *        mappedBy indica que o relacionamento é gerenciado pelo campo 'empresa'
     *        na entidade Endereco.
     */
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Endereco> enderecos = new HashSet<>(); // Conjunto de endereços da empresa

    /**
     * @brief Coleção de strings para armazenar múltiplos números de telefone.
     *        Será mapeada para uma tabela separada 'tb_empresa_telefones'.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_empresa_telefones", joinColumns = @JoinColumn(name = "empresa_id"))
    @Column(name = "numero_telefone", length = 20)
    private Set<String> telefones = new HashSet<>(); // Conjunto de números de telefone da empresa
}