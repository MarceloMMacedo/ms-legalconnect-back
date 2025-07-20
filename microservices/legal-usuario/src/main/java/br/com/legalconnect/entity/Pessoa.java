package br.com.legalconnect.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set; // Usaremos Set para evitar duplicatas e garantir unicidade

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Pessoa
 * @brief Entidade base abstrata para todas as pessoas no sistema (Profissional,
 *        Cliente, Administrador).
 *        Contém dados comuns a todos os tipos de pessoas e um relacionamento
 *        OneToOne com a entidade User.
 *        Utiliza estratégia de herança JOINED para mapear subclasses em tabelas
 *        separadas.
 */
@Entity
@Table(name = "tb_pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Pessoa extends BaseEntity {

    /**
     * @brief Relacionamento um-para-um com a entidade User.
     *        Este lado é o dono do relacionamento, e a coluna 'user_id' será criada
     *        em 'tb_pessoa'.
     *        O CascadeType.ALL garante que operações no User (como deleção) se
     *        propaguem para Pessoa.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User usuario; // O usuário associado a esta pessoa

    @Column(name = "nome_completo", nullable = false, length = 255)
    private String nomeCompleto; // Nome completo da pessoa

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf; // Número do Cadastro de Pessoa Física (CPF), único por pessoa

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento; // Data de nascimento da pessoa

    /**
     * @brief Relacionamento um-para-muitos com a entidade Endereco.
     *        Uma pessoa pode ter múltiplos endereços (residencial, comercial,
     *        etc.).
     *        CascadeType.ALL garante que operações nos Enderecos se propaguem para
     *        Pessoa.
     *        mappedBy indica que o relacionamento é gerenciado pelo campo 'pessoa'
     *        na entidade Endereco.
     */
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Endereco> enderecos = new HashSet<>(); // Conjunto de endereços da pessoa

    /**
     * @brief Coleção de strings para armazenar múltiplos números de telefone.
     *        Será mapeada para uma tabela separada 'tb_pessoa_telefones'.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_pessoa_telefones", joinColumns = @JoinColumn(name = "pessoa_id"))
    @Column(name = "numero_telefone", length = 20)
    private Set<String> telefones = new HashSet<>(); // Conjunto de números de telefone da pessoa
}