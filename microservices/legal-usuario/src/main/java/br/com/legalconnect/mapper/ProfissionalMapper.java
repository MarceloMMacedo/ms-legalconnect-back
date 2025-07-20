package br.com.legalconnect.mapper;

import br.com.legalconnect.dto.PlanoResponseDTO;
import br.com.legalconnect.entity.Plano; // Importar Plano da entidade
import br.com.legalconnect.dto.EmpresaResponseDTO; // Importar EmpresaResponseDTO
import br.com.legalconnect.dto.ProfissionalRequestDTO;
import br.com.legalconnect.dto.ProfissionalResponseDTO;
import br.com.legalconnect.entity.Empresa; // Importar Empresa da entidade
import br.com.legalconnect.entity.Profissional;
import br.com.legalconnect.entity.Profissional.StatusProfissional; // Importar o enum correto
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @interface ProfissionalMapper
 * @brief Mapper MapStruct para conversão entre Profissional e seus DTOs.
 */
@Mapper(componentModel = "spring", uses = {PessoaMapper.class, EnderecoMapper.class, UserMapper.class, EmpresaMapper.class})
public interface ProfissionalMapper extends PessoaMapper {

    ProfissionalMapper INSTANCE = Mappers.getMapper(ProfissionalMapper.class);

    /**
     * @brief Mapeia um ProfissionalRequestDTO para uma entidade Profissional.
     * @param dto O DTO de requisição do profissional.
     * @return A entidade Profissional.
     */
    @Mapping(target = "empresa", ignore = true) // Empresa será setada no serviço
    @Mapping(target = "plano", ignore = true)   // Plano será setado no serviço
    @Mapping(target = "roleProfissionals", ignore = true) // Não presente no DTO
    Profissional toEntity(ProfissionalRequestDTO dto);

    /**
     * @brief Mapeia uma entidade Profissional para um ProfissionalResponseDTO.
     * @param entity A entidade Profissional.
     * @return O DTO de resposta do profissional.
     */
    @Mapping(source = "empresa", target = "empresa")
    @Mapping(source = "plano", target = "plano")
    ProfissionalResponseDTO toResponseDTO(Profissional entity);

    /**
     * @brief Mapeia uma entidade Plano para um PlanoResponseDTO.
     * @param plano A entidade Plano.
     * @return O DTO de resposta do Plano.
     */
    PlanoResponseDTO toPlanoResponseDTO(Plano plano);
}