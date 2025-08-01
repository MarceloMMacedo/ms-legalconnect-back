//
// Serviço de domínio que interage com o repositório JPA de patrocinadores.
// Contém a lógica de negócio principal e a comunicação direta com a camada de infraestrutura (repositórios).
//
package br.com.legalconnect.patrocinio.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioEscritorio;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioEvento;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioItem;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioNoticia;
import br.com.legalconnect.patrocinio.infrastructure.repository.PatrocinioJpaRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de domínio responsável por gerenciar a persistência dos
 * patrocinadores.
 * Esta camada deve ser agnóstica à API e se concentrar em operações de dados.
 */
@Service
@RequiredArgsConstructor
public class PatrocinioDomainService {

    private static final Logger log = LoggerFactory.getLogger(PatrocinioDomainService.class);
    private final PatrocinioJpaRepository repository;

    /**
     * Busca todos os patrocinadores com status ATIVO.
     *
     * @return Uma lista de entidades PatrocinioItem com o status ATIVO.
     */
    public List<PatrocinioItem> findActivePatrocinios() {
        log.debug("Buscando patrocinadores ativos no repositório.");
        return repository.findByStatus(PatrocinioStatus.ACTIVE);
    }

    /**
     * Busca todos os patrocinadores, independente do status.
     *
     * @return Uma lista de todas as entidades PatrocinioItem.
     */
    public List<PatrocinioItem> findAllPatrocinios() {
        log.debug("Buscando todos os patrocinadores no repositório.");
        return repository.findAll();
    }

    /**
     * Busca um patrocinador pelo seu ID.
     *
     * @param id O ID do patrocinador.
     * @return Um Optional contendo a entidade PatrocinioItem, se encontrada.
     */
    public Optional<PatrocinioItem> findPatrocinioById(UUID id) {
        log.debug("Buscando patrocinador por ID: {}", id);
        return repository.findById(id);
    }

    /**
     * Cria um novo patrocinador no banco de dados.
     *
     * @param patrocinio A entidade PatrocinioItem a ser salva.
     * @return A entidade PatrocinioItem salva, com o ID gerado.
     */
    public PatrocinioItem createPatrocinio(PatrocinioItem patrocinio) {
        log.info("Salvando novo patrocinador do tipo {}: {}", patrocinio.getTipo(), patrocinio.toString());
        return repository.save(patrocinio);
    }

    /**
     * Atualiza um patrocinador existente.
     *
     * @param id                O ID da entidade a ser atualizada.
     * @param updatedPatrocinio A entidade PatrocinioItem com os dados atualizados.
     * @return A entidade PatrocinioItem salva.
     */
    public PatrocinioItem updatePatrocinio(UUID id, PatrocinioItem updatedPatrocinio) {
        log.info("Atualizando patrocinador com ID: {}", id);
        return repository.findById(id).map(existing -> {
            // A lógica de atualização é genérica, mas a JPA cuida do tipo concreto
            existing.setLink(updatedPatrocinio.getLink());
            existing.setStatus(updatedPatrocinio.getStatus());

            // A cópia dos dados específicos para cada tipo de patrocínio
            if (existing instanceof PatrocinioEvento && updatedPatrocinio instanceof PatrocinioEvento) {
                PatrocinioEvento existingEvento = (PatrocinioEvento) existing;
                PatrocinioEvento updatedEvento = (PatrocinioEvento) updatedPatrocinio;
                existingEvento.setTitulo(updatedEvento.getTitulo());
                existingEvento.setDataEvento(updatedEvento.getDataEvento());
                existingEvento.setImagemUrl(updatedEvento.getImagemUrl());
            } else if (existing instanceof PatrocinioEscritorio && updatedPatrocinio instanceof PatrocinioEscritorio) {
                PatrocinioEscritorio existingEscritorio = (PatrocinioEscritorio) existing;
                PatrocinioEscritorio updatedEscritorio = (PatrocinioEscritorio) updatedPatrocinio;
                existingEscritorio.setNome(updatedEscritorio.getNome());
                existingEscritorio.setSlogan(updatedEscritorio.getSlogan());
                existingEscritorio.setLogoUrl(updatedEscritorio.getLogoUrl());
            } else if (existing instanceof PatrocinioNoticia && updatedPatrocinio instanceof PatrocinioNoticia) {
                PatrocinioNoticia existingNoticia = (PatrocinioNoticia) existing;
                PatrocinioNoticia updatedNoticia = (PatrocinioNoticia) updatedPatrocinio;
                existingNoticia.setTitulo(updatedNoticia.getTitulo());
                existingNoticia.setImagemUrl(updatedNoticia.getImagemUrl());
                existingNoticia.setDataPublicacao(updatedNoticia.getDataPublicacao());
            } else {
                throw new BusinessException(ErrorCode.DADOS_INVALIDOS,
                        "Tentativa de atualizar um tipo de patrocínio com dados incompatíveis.");
            }
            return repository.save(existing);
        }).orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, "Patrocinador não encontrado."));
    }

    /**
     * Altera o status de um patrocinador.
     *
     * @param id     ID do patrocinador.
     * @param status O novo status.
     * @return A entidade PatrocinioItem com o status alterado.
     * @throws BusinessException se o patrocinador não for encontrado.
     */
    public PatrocinioItem updatePatrocinioStatus(UUID id, PatrocinioStatus status) {
        log.info("Tentando alterar o status do patrocinador {} para {}", id, status);
        return repository.findById(id)
                .map(patrocinio -> {
                    patrocinio.setStatus(status);
                    return repository.save(patrocinio);
                })
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, "Patrocinador não encontrado."));
    }

    /**
     * Exclui um patrocinador pelo ID.
     *
     * @param id O ID do patrocinador a ser excluído.
     * @throws BusinessException se o patrocinador não for encontrado.
     */
    public void deletePatrocinio(UUID id) {
        log.info("Excluindo patrocinador com ID: {}", id);
        if (!repository.existsById(id)) {
            throw new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, "Patrocinador não encontrado.");
        }
        repository.deleteById(id);
    }
}