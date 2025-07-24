package br.com.legalconnect.advogado.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.legalconnect.advogado.domain.modal.entity.CertificacaoEntity;

public interface CertificacaoRepository {

    // Salva ou atualiza uma Certificação para um Profissional.
    CertificacaoEntity save(CertificacaoEntity certificacao, UUID profissionalId);

    // Busca uma Certificação pelo seu ID e pelo ID do Profissional.
    Optional<CertificacaoEntity> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    // Busca todas as Certificações de um Profissional.
    List<CertificacaoEntity> findAllByProfissionalId(UUID profissionalId);

    // Deleta uma Certificação pelo seu ID e pelo ID do Profissional.
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}