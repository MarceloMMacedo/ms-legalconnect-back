package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.IdiomaEntity;

public interface IdiomaRepository {

    // Salva ou atualiza um Idioma.
    IdiomaEntity save(IdiomaEntity idioma);

    // Busca um Idioma pelo seu ID.
    Optional<IdiomaEntity> findById(UUID id);

    // Busca um Idioma pelo nome.
    Optional<IdiomaEntity> findByNome(String nome);

    // Busca todos os Idiomas.
    List<IdiomaEntity> findAll();

    // Deleta um Idioma pelo seu ID.
    void deleteById(UUID id);
}