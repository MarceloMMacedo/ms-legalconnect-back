package br.com.legalconnect.auth.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory

/**
 * @class PasswordEncoderUtil
 * @brief Utilitário para criptografia e verificação de senhas usando BCrypt.
 *
 * Esta classe encapsula a funcionalidade do `BCryptPasswordEncoder` do
 * Spring Security,
 * garantindo que as senhas sejam armazenadas de forma segura (hashed) e
 * possam ser
 * verificadas corretamente. É um `@Service` para ser injetável em outras
 * classes.
 */
@Service // Marca como um componente de serviço gerenciado pelo Spring
public class PasswordEncoderUtil {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderUtil.class); // Instância do Logger

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Criando bean BCryptPasswordEncoder.");
        return new BCryptPasswordEncoder();
    }

    /**
     * @brief Criptografa uma senha em texto puro (raw password).
     *
     * Utiliza o algoritmo BCrypt para gerar um hash seguro da senha.
     *
     * @param rawPassword A senha em texto puro fornecida pelo usuário.
     * @return A representação criptografada (hashed) da senha.
     */
    public String encode(CharSequence rawPassword) {
        log.debug("Criptografando senha.");
        return passwordEncoder().encode(rawPassword);
    }

    /**
     * @brief Verifica se uma senha em texto puro corresponde a uma senha
     * criptografada (hashed).
     *
     * Compara o hash da senha fornecida com o hash armazenado, sem
     * descriptografar.
     *
     * @param rawPassword     A senha em texto puro fornecida pelo usuário (ex: no
     * login).
     * @param encodedPassword A senha criptografada (hashed) armazenada no banco de
     * dados.
     * @return `true` se as senhas corresponderem, `false` caso contrário.
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.debug("Verificando correspondência de senha.");
        return passwordEncoder().matches(rawPassword, encodedPassword);
    }

}