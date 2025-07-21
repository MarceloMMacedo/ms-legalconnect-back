package br.com.legalconnect.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.dto.UserRequestDTO;
import br.com.legalconnect.dto.UserResponseDTO;
import br.com.legalconnect.entity.User;

/**
 * @interface UserMapper
 * @brief Mapper MapStruct para conversão entre User e seus DTOs.
 */
@Mapper(componentModel = "spring") // Integração com Spring para injeção de dependência
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * @brief Converte um UserRequestDTO para uma entidade User.
     *        Ignora campos da entidade User que não estão presentes no
     *        UserRequestDTO.
     * @param dto O DTO de requisição do usuário.
     * @return A entidade User.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "nomeCompleto", ignore = true) // Não presente no DTO de requisição
    @Mapping(target = "cpf", ignore = true) // Não presente no DTO de requisição
    @Mapping(target = "telefone", ignore = true) // Não presente no DTO de requisição
    // @Mapping(target = "senhaHash", source = "password") // Mapeia password do DTO
    // para senhaHash da entidade
    @Mapping(target = "fotoUrl", ignore = true) // Não presente no DTO de requisição
    User toEntity(UserRequestDTO dto);

    /**
     * @brief Converte uma entidade User para um UserResponseDTO.
     *        Ignora campos da entidade User que não estão presentes no
     *        UserResponseDTO.
     * @param entity A entidade User.
     * @return O DTO de resposta do usuário.
     */
    @Mapping(target = "email", source = "email")
    UserResponseDTO toResponseDTO(User entity);
}