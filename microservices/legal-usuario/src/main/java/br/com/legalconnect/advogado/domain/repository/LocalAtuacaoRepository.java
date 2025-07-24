package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.LocalAtuacaoEntity;

public interface LocalAtuacaoRepository {

    // Salva ou atualiza um Local de Atuação.
    LocalAtuacaoEntity save(LocalAtuacaoEntity localAtuacao);

    // Busca um Local de Atuação pelo seu ID.
    Optional<LocalAtuacaoEntity> findById(UUID id);

    // Busca um Local de Atuação pelo nome.
    Optional<LocalAtuacaoEntity> findByNome(String nome);

    // Busca todos os Locais de Atuação.
    List<LocalAtuacaoEntity> findAll();

    // Deleta um Local de Atuação pelo seu ID.
    void deleteById(UUID id);
}