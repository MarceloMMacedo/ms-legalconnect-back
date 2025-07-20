package br.com.legalconnect.mapper;

import br.com.legalconnect.dto.ClienteRequestDTO;
import br.com.legalconnect.dto.ClienteResponseDTO;
import br.com.legalconnect.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @interface ClienteMapper
 * @brief Mapper MapStruct para conversão entre Cliente e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {PessoaMapper.class, EnderecoMapper.class, UserMapper.class})
public interface ClienteMapper extends PessoaMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    /**
     * @brief Mapeia um ClienteRequestDTO para uma entidade Cliente.
     * @param dto O DTO de requisição do cliente.
     * @return A entidade Cliente.
     */
    Cliente toEntity(ClienteRequestDTO dto);

    /**
     * @brief Mapeia uma entidade Cliente para um ClienteResponseDTO.
     * @param entity A entidade Cliente.
     * @return O DTO de resposta do cliente.
     */
    @Mapping(source = "status", target = "status")
    @Mapping(target = "tipo", ignore = true) // O campo 'tipo' não existe na entidade Cliente, apenas no DTO de resposta
    ClienteResponseDTO toResponseDTO(Cliente entity);
}