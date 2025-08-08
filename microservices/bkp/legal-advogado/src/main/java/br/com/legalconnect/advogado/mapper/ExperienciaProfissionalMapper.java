package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.ExperienciaProfissional;
import br.com.legalconnect.advogado.dto.request.ExperienciaProfissionalRequestDTO;
import br.com.legalconnect.advogado.dto.response.ExperienciaProfissionalResponseDTO;

/**
 * Mapper MapStruct para a entidade ExperienciaProfissional e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface ExperienciaProfissionalMapper {
    ExperienciaProfissionalMapper INSTANCE = Mappers.getMapper(ExperienciaProfissionalMapper.class);

    /**
     * Mapeia um ExperienciaProfissionalRequestDTO para uma entidade
     * ExperienciaProfissional.
     * Os campos 'profissional' e 'tenantId' devem ser setados no serviço.
     *
     * @param dto O DTO de requisição.
     * @return A entidade ExperienciaProfissional correspondente.
     */
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    ExperienciaProfissional toEntity(ExperienciaProfissionalRequestDTO dto);

    /**
     * Mapeia uma entidade ExperienciaProfissional para um
     * ExperienciaProfissionalResponseDTO.
     *
     * @param entity A entidade ExperienciaProfissional.
     * @return O DTO de resposta correspondente.
     */
    ExperienciaProfissionalResponseDTO toResponseDTO(ExperienciaProfissional entity);

    /**
     * Atualiza uma entidade ExperienciaProfissional existente com os dados de um
     * ExperienciaProfissionalRequestDTO.
     * Os campos 'profissional' e 'tenantId' não são atualizados via DTO.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade ExperienciaProfissional a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID não deve ser atualizado pelo DTO
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateEntityFromDto(ExperienciaProfissionalRequestDTO dto, @MappingTarget ExperienciaProfissional entity);
}