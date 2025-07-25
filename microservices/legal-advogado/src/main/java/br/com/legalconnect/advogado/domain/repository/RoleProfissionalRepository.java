package br.com.legalconnect.advogado.domain.repository;

import br.com.legalconnect.advogado.domain.modal.entity.RoleProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repositório para a entidade {@link RoleProfissional}.
 * Gerencia operações de persistência para os papéis (roles) dos profissionais.
 */
@Repository
public interface RoleProfissionalRepository extends JpaRepository<RoleProfissional, UUID> {

    /**
     * Busca uma Role de Profissional pelo nome.
     *
     * @param name O nome da role.
     * @return Um Optional contendo a RoleProfissional, se encontrada.
     */
    Optional<RoleProfissional> findByName(String name);

    /**
     * Busca todas as Roles de Profissional associadas a um determinado tenant.
     *
     * @param tenantId O ID do tenant.
     * @return Uma lista de RoleProfissional.
     */
    List<RoleProfissional> findAllByTenantId(UUID tenantId);
}