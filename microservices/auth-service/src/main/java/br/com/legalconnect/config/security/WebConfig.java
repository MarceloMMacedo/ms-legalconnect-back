package br.com.legalconnect.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// common/config/WebConfig.java
/**
 * @class WebConfig
 * @brief Classe de configuração web geral para a aplicação Spring Boot.
 *
 *        Esta configuração é usada para definir regras de CORS (Cross-Origin
 *        Resource Sharing),
 *        permitindo que aplicações frontend em domínios diferentes acessem a
 *        API de forma segura.
 */
@Configuration
class WebConfig implements WebMvcConfigurer { // Removido 'public' para estar no mesmo arquivo

    /**
     * @brief Configura as regras de CORS para a aplicação.
     *
     *        Permite requisições de qualquer origem (em ambiente de
     *        desenvolvimento),
     *        define os métodos HTTP permitidos, os cabeçalhos permitidos e o tempo
     *        de cache para preflight requests. Em produção, `allowedOrigins` deve
     *        ser restrito aos domínios do frontend.
     *
     * @param registry O registro de CORS para adicionar as configurações.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica as regras de CORS a todos os endpoints da API
                .allowedOrigins("*") // Em produção, substituir por domínios específicos (ex:
                                     // "https://meufrontend.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos na requisição
                .allowCredentials(false) // Não permite o envio de cookies de credenciais (para a maioria das APIs
                                         // RESTful com JWT)
                .maxAge(3600); // Tempo máximo (em segundos) que os resultados do preflight request (OPTIONS)
                               // podem ser cacheados pelo navegador
    }
}
