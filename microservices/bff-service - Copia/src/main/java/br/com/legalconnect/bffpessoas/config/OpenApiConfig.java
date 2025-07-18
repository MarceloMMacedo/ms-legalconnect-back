package br.com.legalconnect.bffpessoas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class OpenApiConfig
 * @brief Classe de configuração para a documentação da API do BFF usando OpenAPI (Swagger UI).
 *
 * Esta configuração define informações básicas sobre a API, como título,
 * versão, contato, licença e, crucialmente, as configurações de segurança para
 * JWT (Bearer Token), permitindo que o Swagger UI envie tokens de autenticação.
 */
@Configuration
public class OpenApiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    /**
     * @brief Define o bean OpenAPI para customizar a documentação.
     *
     * Configura metadados da API e um esquema de segurança global para JWT
     * (Bearer Token), que será exibido no Swagger UI para permitir a inserção
     * do token de autenticação.
     * @return Uma instância de `OpenAPI` com as configurações da API.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Nome do esquema de segurança para o JWT
        log.info("Configurando OpenAPI (Swagger) para o BFF.");

        return new OpenAPI()
                .info(new Info()
                        .title("LegalConnect BFF Pessoas API") // Título da API
                        .version("1.0.0") // Versão da API
                        .description("API Backend For Frontend (BFF) para o gerenciamento de usuários e perfis no LegalConnect. " +
                                "Esta API orquestra chamadas para os microsserviços de autenticação e usuários, " +
                                "garantindo validação de token e propagação de contexto.") // Descrição detalhada da API
                        .contact(new Contact()
                                .name("Equipe LegalConnect") // Nome da equipe de contato
                                .email("contato@legalconnect.com") // E-mail de contato
                                .url("https://www.legalconnect.com"))) // URL do site da LegalConnect
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // Adiciona o requisito de segurança global
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName) // Nome do esquema de segurança
                                .type(SecurityScheme.Type.HTTP) // Tipo de segurança: HTTP
                                .scheme("bearer") // Esquema: bearer (para JWT)
                                .bearerFormat("JWT") // Formato do token: JWT
                                .description("Autenticação JWT usando Bearer Token. " +
                                        "Insira seu token JWT no formato 'Bearer SEU_TOKEN_AQUI' para acessar endpoints protegidos."))); // Descrição no Swagger UI
    }
}