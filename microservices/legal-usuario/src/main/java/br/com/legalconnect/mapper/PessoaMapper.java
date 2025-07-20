package br.com.legalconnect.mapper;

import br.com.legalconnect.dto.PessoaResponseDTO;
import br.com.legalconnect.entity.Pessoa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.InheritInverseConfiguration; // Importar se for usar @InheritInverseConfiguration
import org.mapstruct.factory.Mappers;

/**
 * @interface PessoaMapper
 * @brief Mapper MapStruct para conversão entre Pessoa e seus DTOs.
 * Esta é uma interface base para ser estendida por mappers de subclasses de Pessoa.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, EnderecoMapper.class})
public interface PessoaMapper {

    PessoaMapper INSTANCE = Mappers.getMapper(PessoaMapper.class);

    /**
     * @brief Mapeia uma entidade Pessoa para um PessoaResponseDTO.
     * @param entity A entidade Pessoa.
     * @return O DTO de resposta da pessoa.
     */
    @Mapping(source = "usuario", target = "usuario")
    @Mapping(source = "enderecos", target = "enderecos")
    @Mapping(source = "telefones", target = "telefones")
    PessoaResponseDTO toResponseDTO(Pessoa entity);
}