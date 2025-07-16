package br.com.legalconnect.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.user.entity.User;

/**
 * @interface UserRepository
 * @brief Repositório JPA para a entidade User.
 *
 * Fornece operações CRUD e métodos de consulta personalizados para
 * gerenciar
 * todos os usuários da plataforma (clientes, advogados, administradores,
 * etc.).
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * @brief Busca um User pelo seu endereço de e-mail.
     * @param email O endereço de e-mail do usuário.
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByEmail(String email);

    /**
     * @brief Busca um User pelo seu número de CPF.
     * @param cpf O número de CPF do usuário.
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByCpf(String cpf);

    /**
     * @brief Verifica se um usuário com um determinado e-mail já existe.
     * @param email O e-mail a ser verificado.
     * @return true se um usuário com este e-mail já existe, false caso contrário.
     */
    boolean existsByEmail(String email);

    /**
     * @brief Verifica se um usuário com um determinado CPF já existe.
     * @param cpf O CPF a ser verificado.
     * @return true se um usuário com este CPF já existe, false caso contrário.
     */
    boolean existsByCpf(String cpf);

    /**
     * @brief Busca um User pelo seu endereço de e-mail e status.
     * @param email  O endereço de e-mail do usuário.
     * @param status O status do usuário (ex: ACTIVE, INACTIVE).
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByEmailAndStatus(String email, User.UserStatus status);

    /**
     * @brief Busca um User pelo seu número de CPF e status.
     * @param cpf    O número de CPF do usuário.
     * @param status O status do usuário (ex: ACTIVE, INACTIVE).
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByCpfAndStatus(String cpf, User.UserStatus status);
}