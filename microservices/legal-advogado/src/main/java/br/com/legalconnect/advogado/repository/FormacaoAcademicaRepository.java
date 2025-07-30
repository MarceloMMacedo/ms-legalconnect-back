package br.com.legalconnect.advogado.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.FormacaoAcademica;

/**
 * Repositório para a entidade {@link FormacaoAcademica}.
 * Gerencia operações de persistência para as formações acadêmicas de um
 * profissional.
 */
@Repository
public interface FormacaoAcademicaRepository extends JpaRepository<FormacaoAcademica, UUID> {

    /**
     * Busca uma Formação Acadêmica pelo seu ID e pelo ID do Profissional associado.
     *
     * @param id             O ID da formação acadêmica.
     * @param profissionalId O ID do profissional.
     * @return Um Optional contendo a Formação Acadêmica, se encontrada.
     */
    Optional<FormacaoAcademica> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    /**
     * Busca todas as Formações Acadêmicas de um Profissional.
     *
     * @param profissionalId O ID do profissional.
     * @return Uma lista de Formações Acadêmicas.
     */
    List<FormacaoAcademica> findAllByProfissionalId(UUID profissionalId);

    /**
     * Deleta uma Formação Acadêmica pelo seu ID e pelo ID do Profissional.
     *
     * @param id             O ID da formação acadêmica.
     * @param profissionalId O ID do profissional.
     */
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}