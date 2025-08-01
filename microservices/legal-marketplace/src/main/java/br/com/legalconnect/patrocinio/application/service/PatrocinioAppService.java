//
// Serviço de aplicação para a lógica de negócio dos patrocinadores.
//
package br.com.legalconnect.patrocinio.application.service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.patrocinio.application.dto.*;
import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioEvento;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioEscritorio;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioItem;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioNoticia;
import br.com.legalconnect.patrocinio.domain.service.PatrocinioDomainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para gerenciar a lógica de negócio dos patrocinadores.
 * Atua como uma camada de tradução entre o mundo da API (DTOs) e o mundo do domínio (entidades).
 * Contém a lógica de conversão entre DTOs e entidades de forma polimórfica.
 */
@Service
@RequiredArgsConstructor
public class PatrocinioAppService {

    private static final Logger log = LoggerFactory.getLogger(PatrocinioAppService.class);
    private final PatrocinioDomainService domainService;

    /**
     * Converte uma entidade de domínio PatrocinioItem para um DTO de resposta polimórfico.
     * @param entity A entidade a ser convertida.
     * @return O DTO de resposta.
     */
    private PatrocinioResponseDTO toResponseDTO(PatrocinioItem entity) {
        if (entity instanceof PatrocinioEvento) {
            PatrocinioEvento evento = (PatrocinioEvento) entity;
            return PatrocinioEventoResponseDTO.builder()
                    .id(evento.getId())
                    .tipo(evento.getTipo())
                    .link(evento.getLink())
                    .status(evento.getStatus().name())
                    .titulo(evento.getTitulo())
                    .dataEvento(evento.getDataEvento())
                    .imagemUrl(evento.getImagemUrl())
                    .createdAt(evento.getCreatedAt())
                    .updatedAt(evento.getUpdatedAt())
                    .build();
        } else if (entity instanceof PatrocinioEscritorio) {
            PatrocinioEscritorio escritorio = (PatrocinioEscritorio) entity;
            return PatrocinioEscritorioResponseDTO.builder()
                    .id(escritorio.getId())
                    .tipo(escritorio.getTipo())
                    .link(escritorio.getLink())
                    .status(escritorio.getStatus().name())
                    .nome(escritorio.getNome())
                    .slogan(escritorio.getSlogan())
                    .logoUrl(escritorio.getLogoUrl())
                    .createdAt(escritorio.getCreatedAt())
                    .updatedAt(escritorio.getUpdatedAt())
                    .build();
        } else if (entity instanceof PatrocinioNoticia) {
            PatrocinioNoticia noticia = (PatrocinioNoticia) entity;
            return PatrocinioNoticiaResponseDTO.builder()
                    .id(noticia.getId())
                    .tipo(noticia.getTipo())
                    .link(noticia.getLink())
                    .status(noticia.getStatus().name())
                    .titulo(noticia.getTitulo())
                    .imagemUrl(noticia.getImagemUrl())
                    .dataPublicacao(noticia.getDataPublicacao())
                    .createdAt(noticia.getCreatedAt())
                    .updatedAt(noticia.getUpdatedAt())
                    .build();
        }
        return null;
    }

    /**
     * Converte um DTO de requisição polimórfico para uma entidade de domínio PatrocinioItem.
     * @param dto O DTO de requisição.
     * @param id O ID da entidade (pode ser null para criação).
     * @return A entidade de domínio.
     */
    private PatrocinioItem toEntity(PatrocinioRequestDTO dto, UUID id) {
        PatrocinioStatus status = PatrocinioStatus.INACTIVE;
        if (dto.getStatus() != null) {
            try {
                status = PatrocinioStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Status inválido '{}' fornecido na requisição. Usando status padrão INACTIVE.", dto.getStatus());
            }
        }

        if ("EVENTO".equalsIgnoreCase(dto.getTipo())) {
            PatrocinioEventoRequestDTO eventoDto = (PatrocinioEventoRequestDTO) dto;
            return PatrocinioEvento.builder()
                    .id(id)
                    .link(eventoDto.getLink())
                    .status(status)
                    .titulo(eventoDto.getTitulo())
                    .dataEvento(eventoDto.getDataEvento())
                    .imagemUrl(eventoDto.getImagemUrl())
                    .build();
        } else if ("ESCRITORIO".equalsIgnoreCase(dto.getTipo())) {
            PatrocinioEscritorioRequestDTO escritorioDto = (PatrocinioEscritorioRequestDTO) dto;
            return PatrocinioEscritorio.builder()
                    .id(id)
                    .link(escritorioDto.getLink())
                    .status(status)
                    .nome(escritorioDto.getNome())
                    .slogan(escritorioDto.getSlogan())
                    .logoUrl(escritorioDto.getLogoUrl())
                    .build();
        } else if ("NOTICIA".equalsIgnoreCase(dto.getTipo())) {
            PatrocinioNoticiaRequestDTO noticiaDto = (PatrocinioNoticiaRequestDTO) dto;
            return PatrocinioNoticia.builder()
                    .id(id)
                    .link(noticiaDto.getLink())
                    .status(status)
                    .titulo(noticiaDto.getTitulo())
                    .imagemUrl(noticiaDto.getImagemUrl())
                    .dataPublicacao(noticiaDto.getDataPublicacao())
                    .build();
        }
        throw new BusinessException(ErrorCode.DADOS_INVALIDOS, "Tipo de patrocínio inválido.");
    }

    /**
     * Busca todos os patrocinadores com status ATIVO.
     *
     * @return Uma lista de DTOs de resposta de patrocinadores ativos.
     */
    public List<PatrocinioResponseDTO> findActivePatrocinios() {
        log.info("Buscando patrocinadores ativos...");
        return domainService.findActivePatrocinios().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos os patrocinadores, independente do status.
     * Requer privilégios de administrador.
     *
     * @return Uma lista de DTOs de resposta de todos os patrocinadores.
     */
    public List<PatrocinioResponseDTO> findAllPatrocinios() {
        log.info("Buscando todos os patrocinadores para administração...");
        return domainService.findAllPatrocinios().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cria um novo patrocinador a partir de um DTO.
     *
     * @param requestDTO O DTO de requisição com os dados do patrocinador.
     * @return O DTO de resposta do patrocinador criado.
     */
    public PatrocinioResponseDTO createPatrocinio(PatrocinioRequestDTO requestDTO) {
        log.info("Iniciando a criação de um novo patrocinador do tipo: {}", requestDTO.getTipo());
        PatrocinioItem newEntity = toEntity(requestDTO, null);
        PatrocinioItem savedEntity = domainService.createPatrocinio(newEntity);
        log.info("Patrocinador criado com sucesso. ID: {}", savedEntity.getId());
        return toResponseDTO(savedEntity);
    }

    /**
     * Atualiza um patrocinador existente.
     *
     * @param id ID do patrocinador a ser atualizado.
     * @param requestDTO O DTO de requisição com os dados atualizados.
     * @return O DTO de resposta do patrocinador atualizado.
     * @throws BusinessException se o patrocinador não for encontrado ou o status for inválido.
     */
    public PatrocinioResponseDTO updatePatrocinio(UUID id, PatrocinioRequestDTO requestDTO) {
        log.info("Iniciando a atualização do patrocinador com ID: {}", id);
        PatrocinioItem existingEntity = domainService.findPatrocinioById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, "Patrocinador não encontrado."));

        // Preenche a entidade existente com os novos dados do DTO
        PatrocinioItem updatedEntity = toEntity(requestDTO, id);
        PatrocinioItem savedEntity = domainService.updatePatrocinio(id, updatedEntity);
        log.info("Patrocinador com ID {} atualizado com sucesso.", savedEntity.getId());
        return toResponseDTO(savedEntity);
    }

    /**
     * Atualiza o status de um patrocinador.
     *
     * @param id ID do patrocinador.
     * @param status A string do novo status (ex: "ACTIVE").
     * @return O DTO de resposta do patrocinador com o status alterado.
     * @throws BusinessException se o patrocinador não for encontrado ou o status for inválido.
     */
    public PatrocinioResponseDTO updatePatrocinioStatus(UUID id, String status) {
        log.info("Tentando atualizar o status do patrocinador com ID {} para: {}", id, status);
        PatrocinioStatus newStatus;
        try {
            newStatus = PatrocinioStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, "Status inválido fornecido: " + status);
        }

        PatrocinioItem updatedEntity = domainService.updatePatrocinioStatus(id, newStatus);
        log.info("Status do patrocinador com ID {} alterado para {}.", id, newStatus);
        return toResponseDTO(updatedEntity);
    }

    /**
     * Exclui um patrocinador pelo ID.
     *
     * @param id O ID do patrocinador a ser excluído.
     */
    public void deletePatrocinio(UUID id) {
        log.warn("Solicitação para excluir patrocinador com ID: {}", id);
        domainService.deletePatrocinio(id);
        log.warn("Patrocinador com ID {} excluído com sucesso.", id);
    }
}