package br.com.legalconnect.advogado.domain.modal.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_profissional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProfissionalEntity extends PessoaEntity {

    @Column(name = "numero_oab", nullable = false, unique = true, length = 50)
    private String numeroOab;

    @Column(name = "status_profissional", nullable = false, length = 50)
    private String statusProfissional; // Mapeia o enum do domínio (StatusProfissional) como String

    @Column(name = "usa_marketplace", nullable = false)
    private Boolean usaMarketplace = false;

    @Column(name = "faz_parte_de_plano", nullable = false)
    private Boolean fazParteDePlano = false;

    @Column(name = "pessoa_id", nullable = false, unique = true)
    private UUID pessoaId; // Referência ao ID da Pessoa (do Person Service)

    @Column(name = "empresa_id")
    private UUID empresaId; // Referência ao ID da Empresa (do Company Service)

    @Column(name = "plano_id", nullable = false)
    private UUID planoId; // Referência ao ID do Plano (do Subscription Service)

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId; // Para isolamento de dados por tenant

    // Relacionamentos com entidades que residem no mesmo Professional Service
    // (infraestrutura)
    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CertificacaoEntity> certificacoes = new HashSet<>();

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<DocumentoEntity> documentos = new HashSet<>();

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ExperienciaProfissionalEntity> experiencias = new HashSet<>();

    @OneToMany(mappedBy = "profissional", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FormacaoAcademicaEntity> formacoes = new HashSet<>();

    // Tabela de junção para locais de atuação (muitos-para-muitos com IDs de Master
    // Data)
    // Se LocalAtuacao for um serviço separado, esta é a forma correta de
    // referenciar por ID.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_profissional_local_atuacao", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "local_atuacao_id", nullable = false)
    private Set<UUID> locaisAtuacaoIds = new HashSet<>();

    // Tabela de junção para áreas de atuação (muitos-para-muitos com IDs de Master
    // Data)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_profissional_area_atuacao", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "area_atuacao_id", nullable = false)
    private Set<UUID> areaAtuacaoIds = new HashSet<>();

    // Tabela de junção para idiomas (muitos-para-muitos com IDs de Master Data)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_profissional_idioma", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "idioma_id", nullable = false)
    private Set<UUID> idiomaIds = new HashSet<>();

    // Tabela de junção para tipos de atendimento (muitos-para-muitos com IDs de
    // Master Data)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tb_profissional_tipo_atendimento", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "tipo_atendimento_id", nullable = false)
    private Set<UUID> tipoAtendimentoIds = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_profissional_roles", joinColumns = @JoinColumn(name = "profissional_id"), inverseJoinColumns = @JoinColumn(name = "role_profissional_id"))
    private Set<RoleProfissionalEntity> roleProfissionals = new HashSet<>();
}