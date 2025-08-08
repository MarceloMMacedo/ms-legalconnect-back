package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.RoleProfissional;

/**
 * @interface RepositorioRoleProfissional
 * @brief Repositório Spring Data JPA para a entidade RoleProfissional.
 *        Fornece métodos CRUD e de busca personalizados para RoleProfissional.
 *        **Nota:** Em um ambiente multi-tenant, os papéis podem ser globais ou
 *        por tenant.
 *        Este repositório assume que a entidade RoleProfissional está no schema
 *        do tenant.
 */
@Repository
public interface RepositorioRoleProfissional extends JpaRepository<RoleProfissional, UUID> {

    /**
     * @brief Busca um RoleProfissional pelo nome.
     * @param nome O nome do papel.
     * @return Um Optional contendo o RoleProfissional, se encontrado.
     */
    Optional<RoleProfissional> findByNome(String nome);
}