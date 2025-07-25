package br.com.legalconnect.advogado.domain.repository;

import br.com.legalconnect.advogado.domain.modal.entity.LocalAtuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repositório para a entidade {@link LocalAtuacao}.
 * Gerencia operações de persistência para os locais de atuação dos profissionais.
 */
@Repository
public interface LocalAtuacaoRepository extends JpaRepository<LocalAtuacao, UUID> {

    /**
     * Busca um Local de Atuação pelo nome.
     *
     * @param nome O nome do local de atuação.
     * @return Um Optional contendo o Local de Atuação, se encontrado.
     */
    Optional<LocalAtuacao> findByNome(String nome);

    /**
     * Busca todos os Locais de Atuação por uma lista de IDs.
     * @param ids Lista de IDs dos locais de atuação.
     * @return Uma lista de Locais de Atuação.
     */
    List<LocalAtuacao> findAllByIdIn(List<UUID> ids);
}