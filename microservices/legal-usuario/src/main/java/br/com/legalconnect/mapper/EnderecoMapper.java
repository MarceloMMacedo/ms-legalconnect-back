package br.com.legalconnect.mapper;

import br.com.legalconnect.dto.EnderecoRequestDTO;
import br.com.legalconnect.dto.EnderecoResponseDTO;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.entity.Endereco.TipoEndereco; // Importa o enum correto
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

/**
 * @interface EnderecoMapper
 * @brief Mapper MapStruct para conversão entre Endereco e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoMapper INSTANCE = Mappers.getMapper(EnderecoMapper.class);

    /**
     * @brief Converte um EnderecoRequestDTO para uma entidade Endereco.
     * @param dto O DTO de requisição do endereço.
     * @return A entidade Endereco.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pessoa", ignore = true) // Será setado no serviço
    @Mapping(target = "empresa", ignore = true) // Será setado no serviço
    Endereco toEntity(EnderecoRequestDTO dto);

    /**
     * @brief Converte uma entidade Endereco para um EnderecoResponseDTO.
     * @param entity A entidade Endereco.
     * @return O DTO de resposta do endereço.
     */
    EnderecoResponseDTO toResponseDTO(Endereco entity);

    /**
     * @brief Converte uma lista de EnderecoRequestDTOs para um Set de entidades Endereco.
     * @param dtoList A lista de DTOs de requisição de endereços.
     * @return Um Set de entidades Endereco.
     */
    Set<Endereco> toEntitySet(List<EnderecoRequestDTO> dtoList);

    /**
     * @brief Converte um Set de entidades Endereco para uma lista de EnderecoResponseDTOs.
     * @param entitySet Um Set de entidades Endereco.
     * @return Uma lista de DTOs de resposta de endereços.
     */
    List<EnderecoResponseDTO> toResponseDTOList(Set<Endereco> entitySet);
}