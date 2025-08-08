package br.com.legalconnect.entity;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class User extends BaseEntity { // Implementa UserDetails

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

    /**
     * @enum UserType
     * @brief Enumeração para categorizar o tipo principal de um usuário.
     *        Define se o usuário é um cliente, advogado ou administrador da
     *        plataforma.
     */
    public enum UserType {
        CLIENTE, // Usuário final que busca serviços jurídicos
        ADVOGADO, // Profissional do direito que oferece serviços
        PLATAFORMA_ADMIN, // Administrador da plataforma com privilégios totais
        SOCIO
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

}