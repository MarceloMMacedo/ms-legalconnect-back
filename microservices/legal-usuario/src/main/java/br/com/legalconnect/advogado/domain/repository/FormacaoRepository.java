package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.FormacaoAcademicaEntity;

public interface FormacaoRepository {

    // Salva ou atualiza uma Formação Acadêmica para um Profissional.
    FormacaoAcademicaEntity save(FormacaoAcademicaEntity formacao, UUID profissionalId);

    // Busca uma Formação Acadêmica pelo seu ID e pelo ID do Profissional.
    Optional<FormacaoAcademicaEntity> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todas as Formações Acadêmicas de um Profissional.
    List<FormacaoAcademicaEntity> findAllByProfissionalId(UUID profissionalId);

    // Deleta uma Formação Acadêmica pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}