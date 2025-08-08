
// shared-configs/src/main/java/br/com/legalconnect/common/config/OpenApiConfig.java
package br.com.legalconnect.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

/**
 * @class OpenApiConfig
 * @brief Configuração do OpenAPI (Swagger) para documentação da API.
 *
 *        Esta classe define os metadados da API, como título, descrição,
 *        versão,
 *        informações de contato e licença. Também configura os servidores para
 *        a documentação, o que é útil em ambientes com API Gateways como Nginx.
 *
 *        A documentação estará disponível em:
 *        - Swagger UI: /swagger-ui.html
 *        - OpenAPI JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    /**
     * @brief Configura o bean principal do OpenAPI.
     * @return Uma instância de OpenAPI com as informações da API.
     */
    @Bean
    public OpenAPI myOpenAPI() {
        // Configuração do servidor de desenvolvimento
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080"); // URL para ambiente de desenvolvimento
        devServer.setDescription("URL do Servidor de Desenvolvimento");

        // Configuração do servidor de produção (exemplo, ajuste conforme seu
        // Nginx/domínio)
        Server prodServer = new Server();
        // Em um cenário real com Nginx, esta seria a URL do seu API Gateway
        prodServer.setUrl("https://api.legalconnect.com.br");
        prodServer.setDescription("URL do Servidor de Produção (via Nginx API Gateway)");

        // Informações de contato
        Contact contact = new Contact();
        contact.setEmail("contato@legalconnect.com.br");
        contact.setName("LegalConnect Suporte");
        contact.setUrl("https://www.legalconnect.com.br");

        // Informações de licença
        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        // Informações gerais da API
        Info info = new Info()
                .title("LegalConnect - Microsserviço de Autenticação API")
                .version("1.0")
                .contact(contact)
                .description(
                        "Documentação da API do microsserviço de autenticação e autorização da plataforma LegalConnect.")
                .termsOfService("https://www.legalconnect.com.br/terms")
                .license(mitLicense);

        // Retorna o objeto OpenAPI completo com informações e servidores
        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer)); // Adiciona múltiplos servidores
    }
}