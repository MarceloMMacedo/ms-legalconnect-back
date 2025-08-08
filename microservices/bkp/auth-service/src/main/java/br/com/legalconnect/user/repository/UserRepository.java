package br.com.legalconnect.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.user.entity.User;

/**
 * @interface UserRepository
 * @brief Repositório para a entidade User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário pelo seu e-mail.
     * 
     * @param email O e-mail do usuário.
     * @return Um Optional contendo o usuário, se encontrado.
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}