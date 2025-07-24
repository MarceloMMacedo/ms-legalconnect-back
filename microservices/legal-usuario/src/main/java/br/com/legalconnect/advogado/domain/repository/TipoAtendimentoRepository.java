package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.TipoAtendimentoEntity;

public interface TipoAtendimentoRepository {

    // Salva ou atualiza um Tipo de Atendimento.
    TipoAtendimentoEntity save(TipoAtendimentoEntity tipoAtendimento);

    // Busca um Tipo de Atendimento pelo seu ID.
    Optional<TipoAtendimentoEntity> findById(UUID id);

    // Busca um Tipo de Atendimento pelo nome.
    Optional<TipoAtendimentoEntity> findByNome(String nome);

    // Busca todos os Tipos de Atendimento.
    List<TipoAtendimentoEntity> findAll();

    // Deleta um Tipo de Atendimento pelo seu ID.
    void deleteById(UUID id);
}