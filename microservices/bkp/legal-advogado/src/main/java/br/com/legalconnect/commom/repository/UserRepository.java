package br.com.legalconnect.commom.repository;

import br.com.legalconnect.commom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade {@link User}.
 * Gerencia operações de persistência para informações de usuários no sistema.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário pelo seu endereço de e-mail.
     * Regra de Negócio: O e-mail é único para cada usuário dentro de um tenant.
     *
     * @param email O endereço de e-mail do usuário.
     * @return Um Optional contendo o User, se encontrado.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se um usuário com o endereço de e-mail fornecido já existe.
     * Regra de Negócio: Garante a unicidade do e-mail no sistema.
     *
     * @param email O endereço de e-mail a ser verificado.
     * @return true se um usuário com o e-mail já existe, false caso contrário.
     */
    boolean existsByEmail(String email);
}