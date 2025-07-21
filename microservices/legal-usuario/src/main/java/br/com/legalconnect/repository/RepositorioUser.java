package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.User;

/**
 * @interface RepositorioUser
 * @brief Repositório Spring Data JPA para a entidade User.
 *        Fornece métodos CRUD e de busca personalizados para User.
 *        **Nota:** Este repositório é crucial para o pré-requisito de
 *        associação de Pessoa a um User existente.
 *        As operações de criação/atualização de User em si são de
 *        responsabilidade de outro microsserviço (ex: auth-service).
 */
@Repository
public interface RepositorioUser extends JpaRepository<User, UUID> {

    /**
     * @brief Busca um User pelo email.
     * @param email O email do usuário.
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByEmail(String email);

    /**
     * @brief Busca um User pelo CPF.
     * @param cpf O CPF do usuário.
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByCpf(String cpf);
}