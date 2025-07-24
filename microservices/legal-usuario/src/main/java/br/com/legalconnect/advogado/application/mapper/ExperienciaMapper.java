package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.legalconnect.advogado.application.dto.request.ExperienciaProfissionalRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.ExperienciaProfissionalResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.ExperienciaProfissionalEntity;

@Mapper(componentModel = "spring")
public interface ExperienciaMapper {

    // Mapeia ExperienciaProfissionalRequestDTO para o modelo de domínio
    // ExperienciaProfissional
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExperienciaProfissionalEntity toDomainModel(ExperienciaProfissionalRequestDTO dto);

    // Atualiza um modelo de domínio ExperienciaProfissional a partir de
    // ExperienciaProfissionalRequestDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainModelFromDto(ExperienciaProfissionalRequestDTO dto,
            @MappingTarget ExperienciaProfissionalEntity experiencia);

    // Mapeia o modelo de domínio ExperienciaProfissional para
    // ExperienciaProfissionalResponseDTO
    ExperienciaProfissionalResponseDTO toResponseDTO(ExperienciaProfissionalEntity experiencia);
}