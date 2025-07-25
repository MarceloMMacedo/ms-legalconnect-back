package br.com.legalconnect.advogado.domain.repository;

import br.com.legalconnect.advogado.domain.modal.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade {@link Documento}.
 * Gerencia operações de persistência para os documentos de um profissional.
 */
@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    /**
     * Busca um Documento pelo seu ID e pelo ID do Profissional associado.
     *
     * @param id O ID do documento.
     * @param profissionalId O ID do profissional.
     * @return Um Optional contendo o Documento, se encontrado.
     */
    Optional<Documento> findByIdAndProfissionalId(UUID id, UUID profissionalId);

    /**
     * Busca todos os Documentos de um Profissional.
     *
     * @param profissionalId O ID do profissional.
     * @return Uma lista de Documentos.
     */
    List<Documento> findAllByProfissionalId(UUID profissionalId);

    /**
     * Deleta um Documento pelo seu ID e pelo ID do Profissional.
     *
     * @param id O ID do documento.
     * @param profissionalId O ID do profissional.
     */
    void deleteByIdAndProfissionalId(UUID id, UUID profissionalId);
}