package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.modal.entity.Certificacao;

/**
 * Repositório para a entidade {@link Certificacao}.
 * Gerencia operações de persistência para as certificações de um profissional.
 */
@Repository
public interface CertificacaoRepository extends JpaRepository<Certificacao, UUID> {

    /**
     * Busca uma Certificação pelo seu ID e pelo ID do Profissional associado.
     *
     * @param id             O ID da certificação.
     * @param profissionalId O ID do profissional.
     * @return Um Optional contendo a Certificação, se encontrada.
     */
    Optional<Certificacao> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    /**
     * Busca todas as Certificações de um Profissional.
     *
     * @param profissionalId O ID do profissional.
     * @return Uma lista de Certificações.
     */
    List<Certificacao> findAllByProfissionalId(UUID profissionalId);

    /**
     * Deleta uma Certificação pelo seu ID e pelo ID do Profissional.
     *
     * @param id             O ID da certificação.
     * @param profissionalId O ID do profissional.
     */
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}