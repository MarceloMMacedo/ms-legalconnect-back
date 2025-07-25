package br.com.legalconnect.advogado.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.modal.entity.Profissional;

/**
 * Repositório para a entidade {@link Profissional}.
 * Gerencia operações de persistência para os profissionais (advogados).
 */
@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {

    /**
     * Busca um Profissional pelo número da OAB.
     *
     * @param numeroOab O número da OAB do profissional.
     * @return Um Optional contendo o Profissional, se encontrado.
     */
    Optional<Profissional> findByNumeroOab(String numeroOab);

    /**
     * Busca todos os Profissionais associados a um determinado tenant.
     *
     * @param tenantId O ID do tenant.
     * @return Uma lista de Profissionais.
     */
    Page<Profissional> findAllByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Verifica se um Profissional existe pelo número da OAB.
     * Regra de Negócio: Garante a unicidade do número da OAB.
     *
     * @param numeroOab O número da OAB a ser verificado.
     * @return true se um Profissional com a OAB já existe, false caso contrário.
     */
    boolean existsByNumeroOab(String numeroOab);

    /**
     * Verifica se um Profissional existe pelo ID da pessoa associada.
     *
     * @param pessoaId O ID da pessoa.
     * @return true se um Profissional com o ID da pessoa existe, false caso
     *         contrário.
     */
    boolean existsByPessoaId(UUID pessoaId);
}