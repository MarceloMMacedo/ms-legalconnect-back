package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.modal.entity.FormacaoAcademica;

@Repository
public interface FormacaoRepository extends JpaRepository<FormacaoAcademica, UUID> {

    // Busca uma Formação Acadêmica pelo seu ID e pelo ID do Profissional.
    Optional<FormacaoAcademica> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todas as Formações Acadêmicas de um Profissional.
    List<FormacaoAcademica> findAllByProfissionalId(UUID profissionalId);

    // Deleta uma Formação Acadêmica pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}