package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.application.dto.response.LocalAtuacaoResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.LocalAtuacao;

/**
 * Mapper MapStruct para a entidade LocalAtuacao e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface LocalAtuacaoMapper {
    LocalAtuacaoMapper INSTANCE = Mappers.getMapper(LocalAtuacaoMapper.class);

    /**
     * Mapeia uma entidade LocalAtuacao para um LocalAtuacaoResponseDTO.
     * 
     * @param entity A entidade LocalAtuacao.
     * @return O DTO de resposta correspondente.
     */
    LocalAtuacaoResponseDTO toResponseDTO(LocalAtuacao entity);

    /**
     * Mapeia um LocalAtuacaoResponseDTO para uma entidade LocalAtuacao.
     * 
     * @param dto O DTO de resposta.
     * @return A entidade LocalAtuacao correspondente.
     */
    LocalAtuacao toEntity(LocalAtuacaoResponseDTO dto);
}