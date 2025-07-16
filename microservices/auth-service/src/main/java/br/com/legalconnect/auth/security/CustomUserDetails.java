package br.com.legalconnect.auth.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.legalconnect.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class CustomUserDetails
 * @brief Implementação personalizada de `UserDetails` do Spring Security.
 *
 *        Esta classe estende a implementação padrão de `UserDetails` para
 *        incluir
 *        informações adicionais do usuário, como o `tenantId`, que é crucial
 *        para a funcionalidade de multitenancy da aplicação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomUserDetails implements UserDetails {

    private String username; // Geralmente o e-mail do usuário
    private String password; // Senha hashed
    private List<SimpleGrantedAuthority> authorities; // Roles/permissões do usuário
    private UUID userId; // ID do usuário
    private UUID tenantId; // ID do tenant ao qual o usuário pertence
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    /**
     * @brief Construtor para facilitar a criação a partir da entidade User.
     * @param user A entidade User para a qual criar o CustomUserDetails.
     */
    public CustomUserDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getSenhaHash();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNome()))
                .collect(Collectors.toList());
        this.userId = user.getId();
        // Garante que o tenantId é obtido do Tenant associado ao User
        this.tenantId = user.getTenant() != null ? user.getTenant().getId() : null;
        this.accountNonExpired = user.getStatus() != User.UserStatus.INACTIVE; // Exemplo de mapeamento
        this.accountNonLocked = user.getStatus() != User.UserStatus.INACTIVE; // Exemplo de mapeamento
        this.credentialsNonExpired = true; // Assumindo que credenciais não expiram por padrão
        this.enabled = user.getStatus() == User.UserStatus.ACTIVE
                || user.getStatus() == User.UserStatus.PENDING_APPROVAL; // Ativo ou aguardando aprovação
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}