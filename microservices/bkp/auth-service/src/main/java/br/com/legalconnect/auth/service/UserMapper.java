package br.com.legalconnect.auth.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import br.com.legalconnect.auth.dto.UserProfileUpdate;
import br.com.legalconnect.auth.dto.UserRegistrationRequest;
import br.com.legalconnect.auth.dto.UserResponseDTO;
import br.com.legalconnect.user.entity.User;

/**
 * @interface UserMapper
 * @brief Mapper para conversão entre a entidade `User` e seus DTOs.
 *
 *        Utiliza MapStruct para gerar automaticamente o código de mapeamento,
 *        incluindo a conversão de `UserType`, `UserStatus` e `Set<Role>` para
 *        Strings.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { RoleMapper.class })
public interface UserMapper {

    UserResponseDTO toDto(User entity);

    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    @Mapping(target = "id", ignore = true)
    // @Mapping(target = "tenant", ignore = true) // Tenant é definido no serviço
    @Mapping(target = "roles", ignore = true) // Roles são definidas no serviço
    @Mapping(target = "senhaHash", ignore = true) // Senha é criptografada no serviço
    @Mapping(target = "userType", ignore = true) // userType é definido no serviço
    @Mapping(target = "status", ignore = true) // status é definido no serviço
    User toEntity(UserRegistrationRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "userType", ignore = true)
    @Mapping(target = "cpf", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDto(UserProfileUpdate dto, @MappingTarget User entity);

    default String mapUserType(User.UserType userType) {
        return userType != null ? userType.name() : null;
    }

    default String mapUserStatus(User.UserStatus userStatus) {
        return userStatus != null ? userStatus.name() : null;
    }

    // // Método para mapear Set<Role> para Set<String> de nomes de roles
    // @Named("roleSetToStringSet")
    // default Set<String> roleSetToStringSet(Set<Role> roles) {
    // if (roles == null) {
    // return null;
    // }
    // return roles.stream()
    // .map(Role::getNome)
    // .collect(Collectors.toSet());
    // }
}
