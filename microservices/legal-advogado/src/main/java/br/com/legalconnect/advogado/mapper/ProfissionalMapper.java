package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.dto.request.ProfissionalCreateRequest;
import br.com.legalconnect.advogado.dto.request.ProfissionalUpdateRequest;
import br.com.legalconnect.advogado.dto.response.ProfissionalResponseDTO;
import br.com.legalconnect.commom.mapper.PessoaMapper;

/**
 * Mapper MapStruct para a entidade Profissional e seus DTOs de requisição e
 * resposta.
 * Lida com mapeamentos complexos e aninhados, delegando para outros mappers
 * quando necessário.
 */
@Mapper(componentModel = "spring", uses = {
        PessoaMapper.class,
        CertificacaoMapper.class,
        DocumentoMapper.class,
        ExperienciaProfissionalMapper.class,
        FormacaoAcademicaMapper.class,
        RoleProfissionalMapper.class
        // Mappers para AreaAtuacao, Idioma, LocalAtuacao, TipoAtendimento NÃO são
        // usados diretamente aqui
        // para mapear UUIDs para DTOs completos, pois isso é responsabilidade do
        // serviço.
})
public interface ProfissionalMapper {
    ProfissionalMapper INSTANCE = Mappers.getMapper(ProfissionalMapper.class);

    /**
     * Mapeia um ProfissionalCreateRequest para uma nova entidade Profissional.
     *
     * @param dto O DTO de requisição para criação.
     * @return A nova entidade Profissional.
     */
    // @Mapping(source = "pessoa.user", target = "usuario") // Mapeia o
    // PessoaRequestDTO (que é 'pessoa') para a superclasse 'usuario'
    Profissional toEntity(ProfissionalCreateRequest dto);

    /**
     * Atualiza uma entidade Profissional existente com os dados de um
     * ProfissionalUpdateRequest.
     *
     * @param dto    O DTO de requisição para atualização.
     * @param entity A entidade Profissional a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID da entidade não deve ser alterado pelo DTO
    @Mapping(source = "pessoa.usuario", target = "usuario") // Mapeia o DTO 'pessoa.usuario' para a superclasse
                                                            // 'usuario'
    @Mapping(target = "pessoaId", ignore = true) // Gerenciado pelo sistema
    @Mapping(target = "statusProfissional", ignore = true) // Definido pela lógica de negócio
    @Mapping(target = "fazParteDePlano", ignore = true) // Definido pela lógica de negócio
    @Mapping(target = "certificacoes", ignore = true) // Gerenciado pelo serviço (criação/atualização/remoção)
    @Mapping(target = "documentos", ignore = true) // Gerenciado pelo serviço
    @Mapping(target = "experiencias", ignore = true) // Gerenciado pelo serviço
    @Mapping(target = "formacoes", ignore = true) // Gerenciado pelo serviço
    @Mapping(target = "roleProfissionals", ignore = true) // Gerenciado pelo serviço
    @Mapping(source = "locaisAtuacaoIds", target = "locaisAtuacaoIds") // List<UUID> para Set<UUID>
    @Mapping(source = "areaAtuacaoIds", target = "areaAtuacaoIds") // List<UUID> para Set<UUID>
    @Mapping(source = "idiomaIds", target = "idiomaIds") // List<UUID> para Set<UUID>
    @Mapping(source = "tipoAtendimentoIds", target = "tipoAtendimentoIds") // List<UUID> para Set<UUID>
    void updateEntityFromDto(ProfissionalUpdateRequest dto, @MappingTarget Profissional entity);

    /**
     * Mapeia uma entidade Profissional para um ProfissionalResponseDTO.
     * Campos de dados mestres (locaisAtuacao, areasAtuacao, idiomas,
     * tiposAtendimento)
     * são ignorados aqui e devem ser populados pela camada de serviço,
     * pois a entidade Profissional armazena apenas os IDs dessas relações.
     *
     * @param entity A entidade Profissional.
     * @return O DTO de resposta correspondente.
     */
    @Mapping(source = "usuario", target = "pessoa") // Mapeia a superclasse 'usuario' para 'pessoa' no DTO
    @Mapping(target = "locaisAtuacao", ignore = true) // Populado pelo serviço
    @Mapping(target = "areasAtuacao", ignore = true) // Populado pelo serviço
    @Mapping(target = "idiomas", ignore = true) // Populado pelo serviço
    @Mapping(target = "tiposAtendimento", ignore = true) // Populado pelo serviço
    @Mapping(source = "roleProfissionals", target = "rolesProfissional") // Mapeia Set<RoleProfissional> para
                                                                         // List<RoleProfissionalResponseDTO>
    ProfissionalResponseDTO toResponseDTO(Profissional entity);
}