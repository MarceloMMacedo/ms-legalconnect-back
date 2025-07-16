package br.com.legalconnect.tenant.repository;

import br.com.legalconnect.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @interface TenantRepository
 * @brief Repositório JPA para a entidade Tenant.
 *
 * Gerencia os tenants (ambientes isolados) na arquitetura de multitenancy da aplicação.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    /**
     * @brief Busca um Tenant pelo nome do seu esquema.
     * @param schemaName O nome do esquema do banco de dados.
     * @return Um Optional contendo o Tenant, se encontrado.
     */
    Optional<Tenant> findBySchemaName(String schemaName);

    /**
     * @brief Busca um Tenant pelo seu nome.
     * @param nome O nome descritivo do tenant.
     * @return Um Optional contendo o Tenant, se encontrado.
     */
    Optional<Tenant> findByNome(String nome);
}