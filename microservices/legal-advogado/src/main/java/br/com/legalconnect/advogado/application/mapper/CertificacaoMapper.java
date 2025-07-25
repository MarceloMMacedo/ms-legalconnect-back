package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.application.dto.request.CertificacaoRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.CertificacaoResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.Certificacao;

/**
 * Mapper MapStruct para a entidade Certificacao e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface CertificacaoMapper {
    CertificacaoMapper INSTANCE = Mappers.getMapper(CertificacaoMapper.class);

    /**
     * Mapeia um CertificacaoRequestDTO para uma entidade Certificacao.
     * O campo 'profissional' e 'tenantId' na entidade devem ser setados no serviço.
     *
     * @param dto O DTO de requisição.
     * @return A entidade Certificacao correspondente.
     */
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Certificacao toEntity(CertificacaoRequestDTO dto);

    /**
     * Mapeia uma entidade Certificacao para um CertificacaoResponseDTO.
     *
     * @param entity A entidade Certificacao.
     * @return O DTO de resposta correspondente.
     */
    CertificacaoResponseDTO toResponseDTO(Certificacao entity);

    /**
     * Atualiza uma entidade Certificacao existente com os dados de um
     * CertificacaoRequestDTO.
     * O campo 'profissional' e 'tenantId' não são atualizados via DTO.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade Certificacao a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID não deve ser atualizado pelo DTO
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    void updateEntityFromDto(CertificacaoRequestDTO dto, @MappingTarget Certificacao entity);
}