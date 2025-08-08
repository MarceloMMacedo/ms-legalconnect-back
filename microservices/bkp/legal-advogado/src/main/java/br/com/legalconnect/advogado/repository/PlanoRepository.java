package br.com.legalconnect.advogado.repository;

import br.com.legalconnect.advogado.domain.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade {@link Plano}.
 * Gerencia operações de persistência para os planos de assinatura.
 */
@Repository
public interface PlanoRepository extends JpaRepository<Plano, UUID> {
    Optional<Plano> findByNome(String nome);
}