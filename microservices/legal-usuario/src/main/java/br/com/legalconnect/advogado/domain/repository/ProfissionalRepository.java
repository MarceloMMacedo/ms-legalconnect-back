package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.ProfissionalEntity;

public interface ProfissionalRepository {

    // Salva ou atualiza um Profissional no repositório.
    ProfissionalEntity save(ProfissionalEntity profissional);

    // Busca um Profissional pelo seu ID.
    Optional<ProfissionalEntity> findById(UUID id);

    // Busca um Profissional pelo número da OAB.
    Optional<ProfissionalEntity> findByNumeroOab(String numeroOab);

    // Busca todos os Profissionais associados a um determinado tenant.
    List<ProfissionalEntity> findAllByTenantId(UUID tenantId);

    // Deleta um Profissional pelo seu ID.
    void deleteById(UUID id);

    // Verifica se um Profissional existe pelo número da OAB.
    boolean existsByNumeroOab(String numeroOab);

    // Verifica se um Profissional existe pelo ID da pessoa associada.
    boolean existsByPessoaId(UUID pessoaId);
}