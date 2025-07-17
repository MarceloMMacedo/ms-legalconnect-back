package br.com.legalconnect.user.repository;

import br.com.legalconnect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @interface UserRepository
 * @brief Repositório para a entidade User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo seu e-mail.
     * @param email O e-mail do usuário.
     * @return Um Optional contendo o usuário, se encontrado.
     */
    Optional<User> findByEmail(String email);
}