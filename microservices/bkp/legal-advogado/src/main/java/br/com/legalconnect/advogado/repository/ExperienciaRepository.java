package br.com.legalconnect.advogado.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.ExperienciaProfissional;

@Repository
public interface ExperienciaRepository extends JpaRepository<ExperienciaProfissional, UUID> {

    // Busca uma Experiência Profissional pelo seu ID e pelo ID do Profissional.
    Optional<ExperienciaProfissional> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todas as Experiências Profissionais de um Profissional.
    List<ExperienciaProfissional> findAllByProfissionalId(UUID profissionalId);

    // Deleta uma Experiência Profissional pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}