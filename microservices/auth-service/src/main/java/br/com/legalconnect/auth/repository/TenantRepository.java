package br.com.legalconnect.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.auth.entity.Tenant;

/**
 * @interface TenantRepository
 * @brief Repositório para a entidade Tenant.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    /**
     * Busca um tenant pelo nome do schema.
     * 
     * @param schemaName O nome do schema do tenant.
     * @return Um Optional contendo o tenant, se encontrado.
     */
    Optional<Tenant> findBySchemaName(String schemaName);

    Optional<Tenant> findById(UUID fromString);

}