package br.com.legalconnect.auth.entity;

import br.com.legalconnect.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Tenant
 * @brief Entidade que representa um tenant (ambiente isolado para
 *        escritórios/advogados).
 *
 *        Esta tabela é global e reside no schema `public`, pois é usada para
 *        descobrir e gerenciar
 *        os diferentes schemas de tenant na aplicação.
 */
@Entity
@Table(name = "tb_tenant", schema = "public") // Agora a tabela tb_tenant reside EXPLICITAMENTE no schema 'public'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true) // Inclui os campos da superclasse no equals e hashCode
public class Tenant extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 255)
    private String nome; // Nome descritivo do tenant (ex: "JusPlatform Principal")

    @Column(name = "schema_name", nullable = false, unique = true, length = 63)
    private String schemaName; // Nome do esquema do banco de dados para multitenancy

    @Enumerated(EnumType.STRING) // Mapeia o enum para String no banco de dados
    @Column(name = "status", nullable = false, length = 50)
    private TenantStatus status; // Status operacional atual do tenant (ex: ACTIVE, INACTIVE)

    /**
     * @enum TenantStatus
     * @brief Enumeração para representar o status operacional de um Tenant.
     *        Define os possíveis estados de um ambiente isolado na plataforma.
     */
    public enum TenantStatus {
        ACTIVE, // Tenant está ativo e operacional
        INACTIVE, // Tenant está inativo e não pode ser acessado
        PENDING_ACTIVATION, // Tenant foi criado, mas aguarda ativação (ex: via e-mail)
        SUSPENDED // Tenant foi suspenso temporariamente (ex: por inadimplência)
    }
}