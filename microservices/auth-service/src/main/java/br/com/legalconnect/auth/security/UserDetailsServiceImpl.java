package br.com.legalconnect.auth.security;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.user.entity.User;
import br.com.legalconnect.user.repository.UserRepository;

/**
 * @class UserDetailsServiceImpl
 * @brief Implementação personalizada de `UserDetailsService` do Spring
 *        Security.
 *
 *        Esta classe é responsável por carregar os detalhes do usuário (e-mail,
 *        senha, roles)
 *        do banco de dados quando um usuário tenta se autenticar.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class); // Instância do Logger

    @Autowired
    private UserRepository userRepository; // Repositório para acessar dados de usuários

    /**
     * @brief Carrega os detalhes do usuário pelo nome de usuário (e-mail).
     * @param email O e-mail do usuário.
     * @return Uma instância de `UserDetails` (CustomUserDetails).
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de
     *                                   dados.
     */
    @Override
    @Transactional(readOnly = true) // Garante que a operação de busca seja somente leitura
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Tentando carregar detalhes do usuário para o e-mail: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado com e-mail: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email);
                });

        log.info("Detalhes do usuário carregados com sucesso para: {}", user.getEmail());
        // Retorna uma instância do seu CustomUserDetails, que implementa UserDetails
        return new CustomUserDetails(user);
    }
}