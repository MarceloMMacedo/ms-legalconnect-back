package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.ExperienciaProfissionalEntity;

public interface ExperienciaRepository {

    // Salva ou atualiza uma Experiência Profissional para um Profissional.
    ExperienciaProfissionalEntity save(ExperienciaProfissionalEntity experiencia, UUID profissionalId);

    // Busca uma Experiência Profissional pelo seu ID e pelo ID do Profissional.
    Optional<ExperienciaProfissionalEntity> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todas as Experiências Profissionais de um Profissional.
    List<ExperienciaProfissionalEntity> findAllByProfissionalId(UUID profissionalId);

    // Deleta uma Experiência Profissional pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}