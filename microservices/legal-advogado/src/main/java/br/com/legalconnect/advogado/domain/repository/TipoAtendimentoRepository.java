package br.com.legalconnect.advogado.domain.repository;

import br.com.legalconnect.advogado.domain.modal.entity.TipoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repositório para a entidade {@link TipoAtendimento}.
 * Gerencia operações de persistência para os tipos de atendimento disponíveis.
 */
@Repository
public interface TipoAtendimentoRepository extends JpaRepository<TipoAtendimento, UUID> {

    /**
     * Busca um Tipo de Atendimento pelo nome.
     *
     * @param nome O nome do tipo de atendimento.
     * @return Um Optional contendo o TipoAtendimento, se encontrado.
     */
    Optional<TipoAtendimento> findByNome(String nome);

    /**
     * Busca todos os Tipos de Atendimento por uma lista de IDs.
     * @param ids Lista de IDs dos tipos de atendimento.
     * @return Uma lista de Tipos de Atendimento.
     */
    List<TipoAtendimento> findAllByIdIn(List<UUID> ids);
}