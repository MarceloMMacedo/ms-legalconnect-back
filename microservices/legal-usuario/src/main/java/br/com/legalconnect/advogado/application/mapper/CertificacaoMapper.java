package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.legalconnect.advogado.application.dto.request.CertificacaoRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.CertificacaoResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.CertificacaoEntity;

@Mapper(componentModel = "spring")
public interface CertificacaoMapper {

    // Mapeia CertificacaoRequestDTO para o modelo de domínio Certificacao

    CertificacaoEntity toDomainModel(CertificacaoRequestDTO dto);

    // Atualiza um modelo de domínio Certificacao a partir de CertificacaoRequestDTO
    @Mapping(target = "id", ignore = true) // ID não é atualizável via update
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainModelFromDto(CertificacaoRequestDTO dto, @MappingTarget CertificacaoEntity certificacao);

    // Mapeia o modelo de domínio Certificacao para CertificacaoResponseDTO
    CertificacaoResponseDTO toResponseDTO(CertificacaoEntity certificacao);
}