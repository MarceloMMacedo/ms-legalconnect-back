package br.com.legalconnect.depoimento.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.depoimento.domain.model.User;

/**
 * Repositório para a entidade {@link User}.
 * Gerencia operações de persistência para informações de usuários no sistema.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

}