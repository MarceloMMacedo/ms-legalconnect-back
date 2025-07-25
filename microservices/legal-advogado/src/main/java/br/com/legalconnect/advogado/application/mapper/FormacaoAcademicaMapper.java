package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.application.dto.request.FormacaoAcademicaRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.FormacaoAcademicaResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.FormacaoAcademica;

/**
 * Mapper MapStruct para a entidade FormacaoAcademica e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface FormacaoAcademicaMapper {
    FormacaoAcademicaMapper INSTANCE = Mappers.getMapper(FormacaoAcademicaMapper.class);

    /**
     * Mapeia um FormacaoAcademicaRequestDTO para uma entidade FormacaoAcademica.
     * Os campos 'profissional' e 'tenantId' devem ser setados no serviço.
     *
     * @param dto O DTO de requisição.
     * @return A entidade FormacaoAcademica correspondente.
     */
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    FormacaoAcademica toEntity(FormacaoAcademicaRequestDTO dto);

    /**
     * Mapeia uma entidade FormacaoAcademica para um FormacaoAcademicaResponseDTO.
     *
     * @param entity A entidade FormacaoAcademica.
     * @return O DTO de resposta correspondente.
     */
    FormacaoAcademicaResponseDTO toResponseDTO(FormacaoAcademica entity);

    /**
     * Atualiza uma entidade FormacaoAcademica existente com os dados de um
     * FormacaoAcademicaRequestDTO.
     * Os campos 'profissional' e 'tenantId' não são atualizados via DTO.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade FormacaoAcademica a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID não deve ser atualizado pelo DTO
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateEntityFromDto(FormacaoAcademicaRequestDTO dto, @MappingTarget FormacaoAcademica entity);
}