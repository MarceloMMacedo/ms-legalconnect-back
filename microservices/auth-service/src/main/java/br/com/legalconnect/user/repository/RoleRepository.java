package br.com.legalconnect.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.user.entity.Role;

/**
 * @interface RoleRepository
 * @brief Repositório JPA para a entidade Role.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    /**
     * @brief Busca uma Role pelo seu nome.
     * @param nome O nome da role (ex: "CLIENTE", "ADVOGADO").
     * @return Um Optional contendo a Role, se encontrada.
     */
    Optional<Role> findByNome(String nome);
}