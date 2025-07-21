package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Administrador;

/**
 * @interface RepositorioAdministrador
 * @brief Repositório Spring Data JPA para a entidade Administrador.
 *        Fornece métodos CRUD e de busca personalizados para Administrador.
 */
@Repository
public interface RepositorioAdministrador extends JpaRepository<Administrador, UUID> {

    /**
     * @brief Busca um Administrador pelo CPF.
     * @param cpf O CPF do administrador.
     * @return Um Optional contendo o Administrador, se encontrado.
     */
    Optional<Administrador> findByCpf(String cpf);

    /**
     * @brief Lista todos os Administradores com paginação.
     * @param pageable Objeto Pageable para configuração de paginação.
     * @return Uma página de Administradores.
     */
    Page<Administrador> findAll(Pageable pageable);
}