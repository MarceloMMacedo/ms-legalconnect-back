package br.com.legalconnect.auth.entity;

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
 * @class Tenant
 * @brief Entidade que representa um tenant na arquitetura multi-tenant.
 * Esta tabela reside no schema 'public' (global).
 */
@Entity
@Table(name = "tb_tenant") // A tabela tb_tenant reside no schema public (global)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Tenant extends BaseEntity {

    @Column(name = "nome", nullable = false, unique = true, length = 255)
    private String nome; // Nome do tenant (ex: "LegalConnect_EmpresaA")

    @Column(name = "schema_name", nullable = false, unique = true, length = 255)
    private String schemaName; // Nome do schema do banco de dados associado a este tenant
}