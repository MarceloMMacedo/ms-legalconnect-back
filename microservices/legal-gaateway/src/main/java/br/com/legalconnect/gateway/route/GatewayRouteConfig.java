package br.com.legalconnect.gateway.route;

import org.springframework.context.annotation.Configuration;

/**
 * Classe para configuração de rotas programáticas do Spring Cloud Gateway.
 * <p>
 * Este arquivo está intencionalmente vazio porque a configuração de rotas principal
 * para este projeto é feita via `application.properties`, conforme solicitado.
 * <p>
 * No entanto, ele serve como um placeholder para a estrutura de pacotes e
 * para indicar o local onde as configurações de rotas programáticas seriam adicionadas,
 * caso fossem necessárias futuramente (por exemplo, para rotas mais dinâmicas
 * ou baseadas em lógica de negócio complexa que não pode ser expressa em propriedades).
 * <p>
 * Para configurar rotas programaticamente, você injetaria {@link org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder}
 * e retornaria um {@link org.springframework.cloud.gateway.route.RouteLocator}
 * de um método anotado com {@code @Bean} (conforme demonstrado no {@link br.com.legalconnect.gateway.config.GatewayConfig}).
 */
@Configuration
public class GatewayRouteConfig {
    // Nenhuma rota programática definida aqui; as rotas são configuradas em application.properties.
}