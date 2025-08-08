//
// Serviço de domínio que interage com o repositório JPA de patrocinadores.
// Contém a lógica de negócio principal e a comunicação direta com a camada de infraestrutura (repositórios).
//
package br.com.legalconnect.patrocinio.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.patrocinio.domain.DestaquesEscritorio;
import br.com.legalconnect.patrocinio.domain.DestaquesEvento;
import br.com.legalconnect.patrocinio.domain.DestaquesItem;
import br.com.legalconnect.patrocinio.domain.DestaquesNoticia;
import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;
import br.com.legalconnect.patrocinio.repository.DestaquesJpaRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de domínio responsável por gerenciar a persistência dos
 * patrocinadores.
 * Esta camada deve ser agnóstica à API e se concentrar em operações de dados.
 */
@Service
@RequiredArgsConstructor
public class DestaquesDomainService {

    private static final Logger log = LoggerFactory.getLogger(DestaquesDomainService.class);
    private final DestaquesJpaRepository repository;

    /**
     * Busca todos os patrocinadores com status ATIVO.
     *
     * @return Uma lista de entidades PatrocinioItem com o status ATIVO.
     */
    public List<DestaquesItem> findActivePatrocinios() {
        log.debug("Buscando patrocinadores ativos no repositório.");
        return repository.findByStatus(PatrocinioStatus.ACTIVE);
    }

    /**
     * Busca todos os patrocinadores, independente do status.
     *
     * @return Uma lista de todas as entidades PatrocinioItem.
     */
    public List<DestaquesItem> findAllPatrocinios() {
        log.debug("Buscando todos os patrocinadores no repositório.");
        return repository.findAll();
    }

    /**
     * Busca um patrocinador pelo seu ID.
     *
     * @param id O ID do patrocinador.
     * @return Um Optional contendo a entidade PatrocinioItem, se encontrada.
     */
    public Optional<DestaquesItem> findPatrocinioById(UUID id) {
        log.debug("Buscando patrocinador por ID: {}", id);
        return repository.findById(id);
    }

    /**
     * Cria um novo patrocinador no banco de dados.
     *
     * @param patrocinio A entidade PatrocinioItem a ser salva.
     * @return A entidade PatrocinioItem salva, com o ID gerado.
     */
    public DestaquesItem createPatrocinio(DestaquesItem patrocinio) {
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
    public DestaquesItem updatePatrocinio(UUID id, DestaquesItem updatedPatrocinio) {
        log.info("Atualizando patrocinador com ID: {}", id);
        return repository.findById(id).map(existing -> {
            // A lógica de atualização é genérica, mas a JPA cuida do tipo concreto
            existing.setLink(updatedPatrocinio.getLink());
            existing.setStatus(updatedPatrocinio.getStatus());

            // A cópia dos dados específicos para cada tipo de patrocínio
            if (existing instanceof DestaquesEvento && updatedPatrocinio instanceof DestaquesEvento) {
                DestaquesEvento existingEvento = (DestaquesEvento) existing;
                DestaquesEvento updatedEvento = (DestaquesEvento) updatedPatrocinio;
                existingEvento.setTitulo(updatedEvento.getTitulo());
                existingEvento.setDataEvento(updatedEvento.getDataEvento());
                existingEvento.setImagemUrl(updatedEvento.getImagemUrl());
            } else if (existing instanceof DestaquesEscritorio && updatedPatrocinio instanceof DestaquesEscritorio) {
                DestaquesEscritorio existingEscritorio = (DestaquesEscritorio) existing;
                DestaquesEscritorio updatedEscritorio = (DestaquesEscritorio) updatedPatrocinio;
                existingEscritorio.setNome(updatedEscritorio.getNome());
                existingEscritorio.setSlogan(updatedEscritorio.getSlogan());
                existingEscritorio.setLogoUrl(updatedEscritorio.getLogoUrl());
            } else if (existing instanceof DestaquesNoticia && updatedPatrocinio instanceof DestaquesNoticia) {
                DestaquesNoticia existingNoticia = (DestaquesNoticia) existing;
                DestaquesNoticia updatedNoticia = (DestaquesNoticia) updatedPatrocinio;
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
    public DestaquesItem updatePatrocinioStatus(UUID id, PatrocinioStatus status) {
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