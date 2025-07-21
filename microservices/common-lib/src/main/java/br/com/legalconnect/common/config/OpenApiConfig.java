package br.com.legalconnect.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @class OpenApiConfig
 * @brief Classe de configuração para a documentação da API usando OpenAPI
 *        (Swagger UI).
 *
 *        Esta configuração define informações básicas sobre a API, como título,
 *        versão,
 *        contato, licença e, crucialmente, as configurações de segurança para
 *        JWT (Bearer Token),
 *        permitindo que o Swagger UI envie tokens de autenticação.
 */
@Configuration
class OpenApiConfig {

        /**
         * @brief Define o bean OpenAPI para customizar a documentação.
         *
         *        Configura metadados da API e um esquema de segurança global para JWT
         *        (Bearer Token),
         *        que será exibido no Swagger UI para permitir a inserção do token de
         *        autenticação.
         *
         * @return Uma instância de `OpenAPI` com as configurações da API.
         */
        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth"; // Nome do esquema de segurança para o JWT

                return new OpenAPI()
                                .info(new Info()
                                                .title("LegalConnect API") // Título da API
                                                .version("1.0.0") // Versão da API
                                                .description("API para a plataforma de marketplace jurídico LegalConnect. "
                                                                +
                                                                "Esta documentação detalha todos os endpoints disponíveis, "
                                                                +
                                                                "incluindo autenticação JWT e regras de autorização.") // Descrição
                                                                                                                       // detalhada
                                                                                                                       // da
                                                                                                                       // API
                                                .contact(new Contact()
                                                                .name("Equipe LegalConnect") // Nome da equipe de
                                                                                             // contato
                                                                .email("contato@legalconnect.com") // E-mail de contato
                                                                .url("https://www.legalconnect.com")) // URL do site da
                                                                                                      // LegalConnect
                                                .license(new License()
                                                                .name("Apache 2.0") // Nome da licença
                                                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))) // URL
                                                                                                                          // da
                                                                                                                          // licença
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Adiciona o
                                                                                                        // requisito de
                                                                                                        // segurança
                                                                                                        // global para
                                                                                                        // todos
                                                                                                        // os endpoints
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                                                .name(securitySchemeName) // Nome do esquema de
                                                                                          // segurança
                                                                .type(SecurityScheme.Type.HTTP) // Tipo de segurança:
                                                                                                // HTTP
                                                                .scheme("bearer") // Esquema: bearer (para JWT)
                                                                .bearerFormat("JWT") // Formato do token: JWT
                                                                .description("Autenticação JWT usando Bearer Token. " +
                                                                                "Insira seu token JWT no formato 'Bearer SEU_TOKEN_AQUI'"))); // Descrição
                                                                                                                                              // no
                                                                                                                                              // Swagger
                                                                                                                                              // UI
        }
}