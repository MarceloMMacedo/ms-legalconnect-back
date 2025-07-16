package br.com.legalconnect.user.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class User
 * @brief Entidade base para todos os usuários (clientes, advogados,
 * administradores de tenant, etc.).
 *
 * Esta entidade representa um usuário na plataforma e está associada a
 * um tenant
 * específico. A tabela de usuário reside nos schemas de tenant.
 */
@Entity
@Table(name = "tb_user") // A tabela tb_user agora reside no schema do tenant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com Tenant
    // Referencia a tabela tb_tenant que está no schema public (global)
    @JoinColumn(name = "tenant_id", nullable = false, referencedColumnName = "id")
    private Tenant tenant; // Identificador do tenant ao qual o usuário pertence

    @Column(name = "nome_completo", nullable = false, length = 255)
    private String nomeCompleto; // Nome completo do usuário

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email; // Endereço de e-mail principal do usuário (único por schema de tenant)

    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf; // Número do Cadastro de Pessoa Física do usuário (único por schema de tenant)

    @Column(name = "telefone", length = 20)
    private String telefone; // Número de telefone de contato do usuário

    @Column(name = "senha_hash", nullable = false, columnDefinition = "TEXT")
    private String senhaHash; // Representação criptografada (hashed) da senha do usuário

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl; // URL da foto de perfil do usuário no S3

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 50)
    private UserType userType; // Categoria principal do usuário (ex: CLIENTE, ADVOGADO)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status; // Status atual da conta do usuário (ex: ACTIVE, PENDING_APPROVAL)

    @ManyToMany(fetch = FetchType.EAGER) // Relacionamento muitos-para-muitos com Role
    @JoinTable(name = "tb_user_role", // Tabela de junção
            joinColumns = @JoinColumn(name = "user_id"), // Coluna que referencia User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Coluna que referencia Role
    )
    private Set<Role> roles = new HashSet<>(); // Papéis/perfil de acesso do usuário

    /**
     * @enum UserType
     * @brief Enumeração para categorizar o tipo principal de um usuário.
     * Define se o usuário é um cliente, advogado ou administrador da
     * plataforma.
     */
    public enum UserType {
        CLIENTE, // Usuário final que busca serviços jurídicos
        ADVOGADO, // Profissional do direito que oferece serviços
        PLATAFORMA_ADMIN // Administrador da plataforma com privilégios totais
    }

    /**
     * @enum UserStatus
     * @brief Enumeração para representar o status atual da conta de um usuário.
     * Define o estado operacional da conta do usuário na plataforma.
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, PENDING_APPROVAL, REJECTED
    }
}