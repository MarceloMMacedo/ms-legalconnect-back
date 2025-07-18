package br.com.legalconnect.user.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.common.dto.BaseEntity;
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
 *        administradores de tenant, etc.).
 *
 *        Esta entidade representa um usuário na plataforma e está associada a
 *        um tenant
 *        específico. A tabela de usuário reside nos schemas de tenant.
 */
@Entity
@Table(name = "tb_user") // A tabela tb_user agora reside no schema do tenant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements UserDetails { // Implementa UserDetails

    private static final Logger log = LoggerFactory.getLogger(User.class);

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
     *        Define se o usuário é um cliente, advogado ou administrador da
     *        plataforma.
     */
    public enum UserType {
        CLIENTE, // Usuário final que busca serviços jurídicos
        ADVOGADO, // Profissional do direito que oferece serviços
        PLATAFORMA_ADMIN // Administrador da plataforma com privilégios totais
    }

    /**
     * @enum UserStatus
     * @brief Enumeração para representar o status atual da conta de um usuário.
     *        Define o estado operacional da conta do usuário na plataforma.
     */
    public enum UserStatus {
        ACTIVE, INACTIVE, PENDING_APPROVAL, REJECTED, PENDING
    }

    // --- Implementação de UserDetails ---

    /**
     * Retorna as autoridades concedidas ao usuário.
     * 
     * @return Coleção de GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        log.trace("Obtendo autoridades para o usuário: {}", this.email);
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Retorna a senha usada para autenticar o usuário.
     * 
     * @return A senha criptografada.
     */
    @Override
    public String getPassword() {
        return this.senhaHash;
    }

    /**
     * Retorna o nome de usuário usado para autenticar o usuário.
     * Neste caso, o e-mail do usuário.
     * 
     * @return O e-mail do usuário.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indica se a conta do usuário não expirou.
     * 
     * @return True se a conta é válida (não expirada), false caso contrário.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Adapte esta lógica conforme a necessidade do projeto (ex: baseado em data de
                     // expiração)
    }

    /**
     * Indica se a conta do usuário não está bloqueada.
     * 
     * @return True se a conta não está bloqueada, false caso contrário.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.status == UserStatus.ACTIVE; // A conta não está bloqueada se o status for ATIVO
    }

    /**
     * Indica se as credenciais do usuário (senha) não expiraram.
     * 
     * @return True se as credenciais são válidas (não expiradas), false caso
     *         contrário.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Adapte esta lógica conforme a necessidade do projeto (ex: baseado em política
                     // de troca de senha)
    }

    /**
     * Indica se o usuário está habilitado.
     * 
     * @return True se o usuário está habilitado, false caso contrário.
     */
    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE; // O usuário está habilitado se o status for ATIVO
    }
}