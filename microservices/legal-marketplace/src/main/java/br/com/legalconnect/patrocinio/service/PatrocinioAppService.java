//
// Serviço de aplicação para a lógica de negócio dos patrocinadores.
//
package br.com.legalconnect.patrocinio.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import br.com.legalconnect.patrocinio.dto.DestaquesEscritorioRequestDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesEscritorioResponseDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesEventoRequestDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesEventoResponseDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesNoticiaRequestDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesNoticiaResponseDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesRequestDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesResponseDTO;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para gerenciar a lógica de negócio dos patrocinadores.
 * Atua como uma camada de tradução entre o mundo da API (DTOs) e o mundo do
 * domínio (entidades).
 * Contém a lógica de conversão entre DTOs e entidades de forma polimórfica.
 */
@Service
@RequiredArgsConstructor
public class PatrocinioAppService {

    private static final Logger log = LoggerFactory.getLogger(PatrocinioAppService.class);
    private final DestaquesDomainService domainService;

    /**
     * Converte uma entidade de domínio PatrocinioItem para um DTO de resposta
     * polimórfico.
     * 
     * @param entity A entidade a ser convertida.
     * @return O DTO de resposta.
     */
    private DestaquesResponseDTO toResponseDTO(DestaquesItem entity) {
        if (entity instanceof DestaquesEvento) {
            DestaquesEvento evento = (DestaquesEvento) entity;
            return DestaquesEventoResponseDTO.builder()
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
        } else if (entity instanceof DestaquesEscritorio) {
            DestaquesEscritorio escritorio = (DestaquesEscritorio) entity;
            return DestaquesEscritorioResponseDTO.builder()
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
        } else if (entity instanceof DestaquesNoticia) {
            DestaquesNoticia noticia = (DestaquesNoticia) entity;
            return DestaquesNoticiaResponseDTO.builder()
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
     * Converte um DTO de requisição polimórfico para uma entidade de domínio
     * PatrocinioItem.
     * 
     * @param dto O DTO de requisição.
     * @param id  O ID da entidade (pode ser null para criação).
     * @return A entidade de domínio.
     */
    private DestaquesItem toEntity(DestaquesRequestDTO dto, UUID id) {
        PatrocinioStatus status = PatrocinioStatus.INACTIVE;
        if (dto.getStatus() != null) {
            try {
                status = PatrocinioStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Status inválido '{}' fornecido na requisição. Usando status padrão INACTIVE.",
                        dto.getStatus());
            }
        }

        if ("EVENTO".equalsIgnoreCase(dto.getTipo())) {
            DestaquesEventoRequestDTO eventoDto = (DestaquesEventoRequestDTO) dto;
            return DestaquesEvento.builder()
                    .id(id)
                    .link(eventoDto.getLink())
                    .status(status)
                    .titulo(eventoDto.getTitulo())
                    .dataEvento(eventoDto.getDataEvento())
                    .imagemUrl(eventoDto.getImagemUrl())
                    .build();
        } else if ("ESCRITORIO".equalsIgnoreCase(dto.getTipo())) {
            DestaquesEscritorioRequestDTO escritorioDto = (DestaquesEscritorioRequestDTO) dto;
            return DestaquesEscritorio.builder()
                    .id(id)
                    .link(escritorioDto.getLink())
                    .status(status)
                    .nome(escritorioDto.getNome())
                    .slogan(escritorioDto.getSlogan())
                    .logoUrl(escritorioDto.getLogoUrl())
                    .build();
        } else if ("NOTICIA".equalsIgnoreCase(dto.getTipo())) {
            DestaquesNoticiaRequestDTO noticiaDto = (DestaquesNoticiaRequestDTO) dto;
            return DestaquesNoticia.builder()
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
    public List<DestaquesResponseDTO> findActivePatrocinios() {
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
    public List<DestaquesResponseDTO> findAllPatrocinios() {
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
    public DestaquesResponseDTO createPatrocinio(DestaquesRequestDTO requestDTO) {
        log.info("Iniciando a criação de um novo patrocinador do tipo: {}", requestDTO.getTipo());
        DestaquesItem newEntity = toEntity(requestDTO, null);
        DestaquesItem savedEntity = domainService.createPatrocinio(newEntity);
        log.info("Patrocinador criado com sucesso. ID: {}", savedEntity.getId());
        return toResponseDTO(savedEntity);
    }

    /**
     * Atualiza um patrocinador existente.
     *
     * @param id         ID do patrocinador a ser atualizado.
     * @param requestDTO O DTO de requisição com os dados atualizados.
     * @return O DTO de resposta do patrocinador atualizado.
     * @throws BusinessException se o patrocinador não for encontrado ou o status
     *                           for inválido.
     */
    public DestaquesResponseDTO updatePatrocinio(UUID id, DestaquesRequestDTO requestDTO) {
        log.info("Iniciando a atualização do patrocinador com ID: {}", id);
        DestaquesItem existingEntity = domainService.findPatrocinioById(id)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, "Patrocinador não encontrado."));

        // Preenche a entidade existente com os novos dados do DTO
        DestaquesItem updatedEntity = toEntity(requestDTO, id);
        DestaquesItem savedEntity = domainService.updatePatrocinio(id, updatedEntity);
        log.info("Patrocinador com ID {} atualizado com sucesso.", savedEntity.getId());
        return toResponseDTO(savedEntity);
    }

    /**
     * Atualiza o status de um patrocinador.
     *
     * @param id     ID do patrocinador.
     * @param status A string do novo status (ex: "ACTIVE").
     * @return O DTO de resposta do patrocinador com o status alterado.
     * @throws BusinessException se o patrocinador não for encontrado ou o status
     *                           for inválido.
     */
    public DestaquesResponseDTO updatePatrocinioStatus(UUID id, String status) {
        log.info("Tentando atualizar o status do patrocinador com ID {} para: {}", id, status);
        PatrocinioStatus newStatus;
        try {
            newStatus = PatrocinioStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, "Status inválido fornecido: " + status);
        }

        DestaquesItem updatedEntity = domainService.updatePatrocinioStatus(id, newStatus);
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