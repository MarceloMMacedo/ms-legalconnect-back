package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.legalconnect.advogado.application.dto.request.FormacaoAcademicaRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.FormacaoAcademicaResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.FormacaoAcademicaEntity;

@Mapper(componentModel = "spring")
public interface FormacaoMapper {

    // Mapeia FormacaoAcademicaRequestDTO para o modelo de domínio FormacaoAcademica
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FormacaoAcademicaEntity toDomainModel(FormacaoAcademicaRequestDTO dto);

    // Atualiza um modelo de domínio FormacaoAcademica a partir de
    // FormacaoAcademicaRequestDTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainModelFromDto(FormacaoAcademicaRequestDTO dto, @MappingTarget FormacaoAcademicaEntity formacao);

    // Mapeia o modelo de domínio FormacaoAcademica para
    // FormacaoAcademicaResponseDTO
    FormacaoAcademicaResponseDTO toResponseDTO(FormacaoAcademicaEntity formacao);
}