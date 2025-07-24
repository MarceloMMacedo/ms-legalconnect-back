package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.AreaAtuacaoEntity;

public interface AreaAtuacaoRepository {

    // Salva ou atualiza uma Área de Atuação.
    AreaAtuacaoEntity save(AreaAtuacaoEntity areaAtuacao);

    // Busca uma Área de Atuação pelo seu ID.
    Optional<AreaAtuacaoEntity> findById(UUID id);

    // Busca uma Área de Atuação pelo nome.
    Optional<AreaAtuacaoEntity> findByNome(String nome);

    // Busca todas as Áreas de Atuação.
    List<AreaAtuacaoEntity> findAll();

    // Deleta uma Área de Atuação pelo seu ID.
    void deleteById(UUID id);
}