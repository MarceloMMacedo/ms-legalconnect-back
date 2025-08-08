package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.Idioma;
import br.com.legalconnect.advogado.dto.response.IdiomaResponseDTO;

/**
 * Mapper MapStruct para a entidade Idioma e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface IdiomaMapper {
    IdiomaMapper INSTANCE = Mappers.getMapper(IdiomaMapper.class);

    /**
     * Mapeia uma entidade Idioma para um IdiomaResponseDTO.
     * 
     * @param entity A entidade Idioma.
     * @return O DTO de resposta correspondente.
     */
    IdiomaResponseDTO toResponseDTO(Idioma entity);

    /**
     * Mapeia um IdiomaResponseDTO para uma entidade Idioma.
     * 
     * @param dto O DTO de resposta.
     * @return A entidade Idioma correspondente.
     */
    Idioma toEntity(IdiomaResponseDTO dto);
}