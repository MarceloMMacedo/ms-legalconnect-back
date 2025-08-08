package br.com.legalconnect.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.dto.EmpresaRequestDTO;
import br.com.legalconnect.dto.EmpresaResponseDTO;
import br.com.legalconnect.entity.Empresa;

/**
 * @interface EmpresaMapper
 * @brief Mapper MapStruct para conversão entre Empresa e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = { EnderecoMapper.class })
public interface EmpresaMapper {

    EmpresaMapper INSTANCE = Mappers.getMapper(EmpresaMapper.class);

    /**
     * @brief Converte um EmpresaRequestDTO para uma entidade Empresa.
     * @param dto O DTO de requisição da empresa.
     * @return A entidade Empresa.
     */
    @Mapping(target = "id", ignore = true) // ID é gerado, não vem do DTO de requisição
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "enderecos", target = "enderecos")
    @Mapping(source = "telefones", target = "telefones")
    Empresa toEntity(EmpresaRequestDTO dto);

    /**
     * @brief Converte uma entidade Empresa para um EmpresaResponseDTO.
     * @param entity A entidade Empresa.
     * @return O DTO de resposta da empresa.
     */
    @Mapping(source = "enderecos", target = "enderecos")
    @Mapping(source = "telefones", target = "telefones")
    EmpresaResponseDTO toResponseDTO(Empresa entity);
}