package br.com.legalconnect.mapper;

import br.com.legalconnect.dto.AdministradorRequestDTO;
import br.com.legalconnect.dto.AdministradorResponseDTO;
import br.com.legalconnect.entity.Administrador;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @interface AdministradorMapper
 * @brief Mapper MapStruct para conversão entre Administrador e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {PessoaMapper.class, EnderecoMapper.class, UserMapper.class})
public interface AdministradorMapper extends PessoaMapper {

    AdministradorMapper INSTANCE = Mappers.getMapper(AdministradorMapper.class);

    /**
     * @brief Mapeia um AdministradorRequestDTO para uma entidade Administrador.
     * @param dto O DTO de requisição do administrador.
     * @return A entidade Administrador.
     */
    Administrador toEntity(AdministradorRequestDTO dto);

    /**
     * @brief Mapeia uma entidade Administrador para um AdministradorResponseDTO.
     * @param entity A entidade Administrador.
     * @return O DTO de resposta do administrador.
     */
    @Mapping(source = "status", target = "status")
    AdministradorResponseDTO toResponseDTO(Administrador entity);
}