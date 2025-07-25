package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.application.dto.response.RoleProfissionalResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.RoleProfissional;

/**
 * Mapper MapStruct para a entidade RoleProfissional e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface RoleProfissionalMapper {
    RoleProfissionalMapper INSTANCE = Mappers.getMapper(RoleProfissionalMapper.class);

    /**
     * Mapeia uma entidade RoleProfissional para um RoleProfissionalResponseDTO.
     * Ignora 'tenantId' na resposta se não for relevante para o frontend.
     *
     * @param entity A entidade RoleProfissional.
     * @return O DTO de resposta correspondente.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleProfissionalResponseDTO toResponseDTO(RoleProfissional entity);

    /**
     * Mapeia um RoleProfissionalResponseDTO para uma entidade RoleProfissional.
     * 'tenantId' deve ser definido pelo serviço.
     *
     * @param dto O DTO de resposta.
     * @return A entidade RoleProfissional correspondente.
     */
    @Mapping(target = "tenantId", ignore = true)
    RoleProfissional toEntity(RoleProfissionalResponseDTO dto);
}