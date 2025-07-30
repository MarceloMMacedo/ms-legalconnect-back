package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.TipoAtendimento;
import br.com.legalconnect.advogado.dto.response.TipoAtendimentoResponseDTO;

/**
 * Mapper MapStruct para a entidade TipoAtendimento e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface TipoAtendimentoMapper {
    TipoAtendimentoMapper INSTANCE = Mappers.getMapper(TipoAtendimentoMapper.class);

    /**
     * Mapeia uma entidade TipoAtendimento para um TipoAtendimentoResponseDTO.
     * 
     * @param entity A entidade TipoAtendimento.
     * @return O DTO de resposta correspondente.
     */
    TipoAtendimentoResponseDTO toResponseDTO(TipoAtendimento entity);

    /**
     * Mapeia um TipoAtendimentoResponseDTO para uma entidade TipoAtendimento.
     * 
     * @param dto O DTO de resposta.
     * @return A entidade TipoAtendimento correspondente.
     */
    TipoAtendimento toEntity(TipoAtendimentoResponseDTO dto);
}