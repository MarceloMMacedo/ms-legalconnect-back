
// shared-configs/src/main/java/br/com/legalconnect/common/config/WebConfig.java
package br.com.legalconnect.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @class WebConfig
 * @brief Configuração global de CORS (Cross-Origin Resource Sharing) para a
 *        aplicação.
 *
 *        Esta classe implementa `WebMvcConfigurer` para personalizar o
 *        comportamento
 *        do Spring MVC, especificamente para permitir requisições CORS de
 *        origens
 *        configuradas. Em um ambiente de produção com Nginx como API Gateway,
 *        o CORS pode ser configurado tanto no Nginx quanto na aplicação.
 *        É uma boa prática ter uma camada de CORS na aplicação também, como
 *        fallback ou para desenvolvimento.
 */
@Configuration
@EnableWebMvc // Habilita a configuração do Spring MVC via Java
public class WebConfig implements WebMvcConfigurer {

    /**
     * @brief Configura as regras de CORS.
     *
     *        Permite requisições de origens específicas, com métodos HTTP
     *        permitidos,
     *        cabeçalhos e credenciais.
     *
     * @param registry O CorsRegistry para adicionar as configurações de CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a configuração a todos os endpoints
                .allowedOrigins(
                        "http://localhost:3000", // Exemplo: Frontend em desenvolvimento
                        "http://localhost:4200", // Exemplo: Outro frontend em desenvolvimento
                        "https://app.legalconnect.com.br", // Exemplo: Frontend em produção
                        "https://admin.legalconnect.com.br" // Exemplo: Painel administrativo em produção
                ) // Permite requisições destas origens
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(true) // Permite o envio de cookies e cabeçalhos de autorização
                .maxAge(3600); // Tempo máximo em segundos que as informações de preflight CORS podem ser
                               // cacheadas
    }
}