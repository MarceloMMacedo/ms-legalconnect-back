package br.com.legalconnect.output.security; // Pacote da lib

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro customizado para ler o cabeçalho 'X-User-Roles' e configurar as
 * autoridades
 * (roles) no contexto de segurança do Spring.
 * Assume que as roles no cabeçalho são separadas por vírgulas (ex:
 * "ROLE_USER,ROLE_ADMIN").
 * Esta classe é parte da biblioteca common-role e será injetada na aplicação
 * consumidora.
 */
@Component
public class UserRolesHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String userRolesHeader = request.getHeader("X-User-Roles");

        if (userRolesHeader != null && !userRolesHeader.trim().isEmpty()) {
            List<SimpleGrantedAuthority> authorities = Arrays.stream(userRolesHeader.split(","))
                    .map(role -> new SimpleGrantedAuthority(role.trim()))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user", null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}