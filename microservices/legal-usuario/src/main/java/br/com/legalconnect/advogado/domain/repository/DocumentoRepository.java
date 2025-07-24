package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.DocumentoEntity;

public interface DocumentoRepository {

    // Salva ou atualiza um Documento para um Profissional.
    DocumentoEntity save(DocumentoEntity documento, UUID profissionalId);

    // Busca um Documento pelo seu ID e pelo ID do Profissional.
    Optional<DocumentoEntity> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todos os Documentos de um Profissional.
    List<DocumentoEntity> findAllByProfissionalId(UUID profissionalId);

    // Deleta um Documento pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}