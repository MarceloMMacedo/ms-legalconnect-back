package br.com.legalconnect.user.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import br.com.legalconnect.user.entity.Role;

/**
 * @interface RoleMapper
 * @brief Mapper para conversão entre a entidade `Role` e suas representações em
 *        String.
 *
 *        Esta interface utiliza MapStruct para gerar automaticamente o código
 *        de mapeamento.
 *        É usada principalmente para converter um `Set<Role>` em um
 *        `Set<String>` contendo
 *        os nomes das roles, o que é útil para DTOs de resposta de usuário.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    /**
     * @brief Mapeia uma entidade Role para o seu nome (String).
     * @param role A entidade Role.
     * @return O nome da role.
     */
    String toRoleName(Role role);

    /**
     * @brief Mapeia um conjunto de entidades Role para um conjunto de nomes de
     *        roles (String).
     * @param roles O conjunto de entidades Role.
     * @return Um conjunto de Strings contendo os nomes das roles.
     */
    @Named("roleSetToStringSet") // Nomeia este método para ser referenciado em outros mappers (ex: UserMapper)
    default Set<String> roleSetToStringSet(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getNome) // Mapeia cada Role para o seu nome
                .collect(Collectors.toSet()); // Coleta os nomes em um Set
    }
}