package br.com.legalconnect.commom.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.commom.dto.request.PessoaRequestDTO;
import br.com.legalconnect.commom.dto.response.PessoaResponseDTO;
import br.com.legalconnect.commom.model.Pessoa;

/**
 * Mapper MapStruct para a entidade Pessoa e seus DTOs.
 * Utiliza UserMapper e EnderecoMapper para mapeamentos aninhados.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, EnderecoMapper.class })
public interface PessoaMapper {
    PessoaMapper INSTANCE = Mappers.getMapper(PessoaMapper.class);

    /**
     * Mapeia um PessoaRequestDTO para uma entidade Pessoa.
     * Os campos 'usuario' e 'enderecos' serão mapeados pelos mappers especificados
     * em 'uses'.
     * A conversão de List para Set para 'telefones' é feita automaticamente pelo
     * MapStruct.
     *
     * @param dto O DTO de requisição.
     * @return A entidade Pessoa correspondente.
     */
    // @Mapping(target = "id", ignore = true) // ID da Pessoa será gerado na criação
    // Pessoa toEntity(PessoaRequestDTO dto);

    /**
     * Mapeia uma entidade Pessoa para um PessoaResponseDTO.
     *
     * @param entity A entidade Pessoa.
     * @return O DTO de resposta correspondente.
     */
    PessoaResponseDTO toResponseDTO(Pessoa entity);

    /**
     * Atualiza uma entidade Pessoa existente com os dados de um PessoaRequestDTO.
     * O 'id' da Pessoa não deve ser atualizado.
     * A atualização de 'usuario' e 'enderecos' deve ser orquestrada no serviço,
     * pois o MapStruct pode não ter o contexto para atualizar entidades aninhadas
     * existentes.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade Pessoa a ser atualizada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true) // O serviço deve gerenciar a atualização do User associado
    @Mapping(target = "enderecos", ignore = true) // O serviço deve gerenciar a atualização dos Enderecos associados
    @Mapping(source = "telefones", target = "telefones") // Converte List<String> para Set<String>
    void updateEntityFromDto(PessoaRequestDTO dto, @MappingTarget Pessoa entity);
}