package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.modal.entity.ExperienciaProfissional;

/**
 * Repositório para a entidade {@link ExperienciaProfissional}.
 * Gerencia operações de persistência para as experiências profissionais de um
 * profissional.
 * O método 'save' é automaticamente fornecido por JpaRepository, não deve ser
 * declarado explicitamente aqui com parâmetros extras.
 */
@Repository
public interface ExperienciaProfissionalRepository extends JpaRepository<ExperienciaProfissional, UUID> {

    /**
     * Busca uma Experiência Profissional pelo seu ID e pelo ID do Profissional
     * associado.
     *
     * @param id             O ID da experiência profissional.
     * @param profissionalId O ID do profissional.
     * @return Um Optional contendo a Experiência Profissional, se encontrada.
     */
    Optional<ExperienciaProfissional> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    /**
     * Busca todas as Experiências Profissionais de um Profissional.
     *
     * @param profissionalId O ID do profissional.
     * @return Uma lista de Experiências Profissionais.
     */
    List<ExperienciaProfissional> findAllByProfissionalId(UUID profissionalId);

    /**
     * Deleta uma Experiência Profissional pelo seu ID e pelo ID do Profissional.
     *
     * @param id             O ID da experiência profissional.
     * @param profissionalId O ID do profissional.
     */
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);

    // IMPORTANTE: NÃO HÁ MÉTODO 'save' EXPLICITAMENTE DECLARADO AQUI.
    // Ele é herdado de JpaRepository e tem a assinatura: S save(S entity);
}