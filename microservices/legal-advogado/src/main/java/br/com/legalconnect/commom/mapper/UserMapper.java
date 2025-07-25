package br.com.legalconnect.commom.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.commom.dto.request.UserRequestDTO;
import br.com.legalconnect.commom.dto.response.UserResponseDTO;
import br.com.legalconnect.commom.model.User;

/**
 * Mapper MapStruct para a entidade User e seus DTOs.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Mapeia um UserRequestDTO para uma entidade User.
     * A senha do DTO é mapeada para 'senhaHash' na entidade.
     * 'userType' e 'userStatus' são definidos pela lógica de negócio e ignorados
     * aqui.
     *
     * @param dto O DTO de requisição.
     * @return A entidade User correspondente.
     */
    @Mapping(source = "senha", target = "senhaHash") // Mapeia a senha do DTO para senhaHash na entidade
    // @Mapping(target = "userType", ignore = true) // Definido pela lógica de
    // negócio
    // @Mapping(target = "userStatus", ignore = true) // Definido pela lógica de
    // negócio
    User toEntity(UserRequestDTO dto);

    /**
     * Mapeia uma entidade User para um UserResponseDTO.
     * 'senhaHash' é ignorado na resposta por segurança.
     *
     * @param entity A entidade User.
     * @return O DTO de resposta correspondente.
     */
    // @Mapping(target = "senha", ignore = true) // 'senhaHash' é um campo interno
    // da entidade, não deve ser exposto no DTO de resposta
    UserResponseDTO toResponseDTO(User entity);

    /**
     * Atualiza uma entidade User existente com os dados de um UserRequestDTO.
     * A senha do DTO é mapeada para 'senhaHash' na entidade.
     * 'userType' e 'userStatus' não são atualizados via DTO.
     *
     * @param dto    O DTO de requisição com os dados para atualização.
     * @param entity A entidade User a ser atualizada.
     */
    @Mapping(target = "id", ignore = true) // ID não deve ser atualizado pelo DTO
    @Mapping(source = "senha", target = "senhaHash")
    // @Mapping(target = "userType", ignore = true)
    // @Mapping(target = "userStatus", ignore = true)
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User entity);
}