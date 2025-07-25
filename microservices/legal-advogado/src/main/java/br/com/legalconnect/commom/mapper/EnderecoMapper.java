package br.com.legalconnect.commom.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.commom.dto.request.EnderecoRequestDTO;
import br.com.legalconnect.commom.dto.response.EnderecoResponseDTO;
import br.com.legalconnect.commom.model.Endereco;

/**
 * Mapper MapStruct para a entidade Endereco e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface EnderecoMapper {
    EnderecoMapper INSTANCE = Mappers.getMapper(EnderecoMapper.class);

    /**
     * Mapeia um EnderecoRequestDTO para uma entidade Endereco.
     * Os campos 'pessoa' e 'empresa' devem ser setados no serviço.
     *
     * @param dto O DTO de requisição.
     * @return A entidade Endereco correspondente.
     */
    @Mapping(target = "pessoa", ignore = true)
    // @Mapping(target = "empresa", ignore = true) // Descomente se Empresa for
    // relevante e existir
    @Mapping(target = "id", source = "id", qualifiedByName = "mapStringToUuid")
    Endereco toEntity(EnderecoRequestDTO dto);

    /**
     * Mapeia uma entidade Endereco para um EnderecoResponseDTO.
     *
     * @param entity A entidade Endereco.
     * @return O DTO de resposta correspondente.
     */
    @Mapping(target = "id", source = "id", qualifiedByName = "mapUuidToString")
    EnderecoResponseDTO toResponseDTO(Endereco entity);

    /**
     * Atualiza uma entidade Endereco existente com os dados de um
     * EnderecoRequestDTO.
     * O campo 'pessoa' e 'empresa' não são atualizados via DTO. O ID não deve ser
     * atualizado.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade Endereco a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID não deve ser atualizado pelo DTO
    @Mapping(target = "pessoa", ignore = true)
    // @Mapping(target = "empresa", ignore = true)
    void updateEntityFromDto(EnderecoRequestDTO dto, @MappingTarget Endereco entity);

    /**
     * Converte uma String (ID do DTO) para um UUID (ID da Entidade).
     * 
     * @param id A string representando o UUID.
     * @return O objeto UUID.
     */
    @Named("mapStringToUuid")
    default UUID mapStringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    /**
     * Converte um UUID (ID da Entidade) para uma String (ID do DTO).
     * 
     * @param id O objeto UUID.
     * @return A string representando o UUID.
     */
    @Named("mapUuidToString")
    default String mapUuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }
}