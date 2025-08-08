package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.AreaAtuacao;
import br.com.legalconnect.advogado.dto.response.AreaAtuacaoResponseDTO;

/**
 * Mapper MapStruct para a entidade AreaAtuacao e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface AreaAtuacaoMapper {
    AreaAtuacaoMapper INSTANCE = Mappers.getMapper(AreaAtuacaoMapper.class);

    /**
     * Mapeia uma entidade AreaAtuacao para um AreaAtuacaoResponseDTO.
     * 
     * @param entity A entidade AreaAtuacao.
     * @return O DTO de resposta correspondente.
     */
    AreaAtuacaoResponseDTO toResponseDTO(AreaAtuacao entity);

    /**
     * Mapeia um AreaAtuacaoResponseDTO para uma entidade AreaAtuacao.
     * Útil para cenários de re-conversão ou testes, embora a criação normalmente
     * venha de um RequestDTO.
     * 
     * @param dto O DTO de resposta.
     * @return A entidade AreaAtuacao correspondente.
     */
    AreaAtuacao toEntity(AreaAtuacaoResponseDTO dto);
}