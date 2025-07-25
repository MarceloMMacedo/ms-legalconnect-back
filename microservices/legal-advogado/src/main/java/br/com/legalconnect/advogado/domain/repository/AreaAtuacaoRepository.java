package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.modal.entity.AreaAtuacao;

@Repository
public interface AreaAtuacaoRepository extends JpaRepository<AreaAtuacao, UUID> {

    /**
     * Busca uma Área de Atuação pelo nome.
     *
     * @param nome O nome da área de atuação.
     * @return Um Optional contendo a Área de Atuação, se encontrada.
     */
    Optional<AreaAtuacao> findByNome(String nome);

    /**
     * 
     * Busca todas as Áreas de Atuação por uma lista de IDs.
     * 
     * @param ids Lista de IDs das áreas de atuação.
     * 
     * @return Uma lista de Áreas de Atuação.
     */
    List<AreaAtuacao> findAllByIdIn(List<UUID> ids);
}