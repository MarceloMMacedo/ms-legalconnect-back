package br.com.legalconnect.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração das rotas do Spring Cloud Gateway.
 * Define como as requisições de entrada são roteadas para os microserviços.
 */
@Configuration
public class GatewayRouteConfig {

        private final GatewayFilter jwtFilter;

        /**
         * Construtor para injeção de dependência do JwtTokenFilter.
         * 
         * @param jwtFilter O filtro JWT para ser aplicado às rotas.
         */
        public GatewayRouteConfig(GatewayFilter jwtFilter) {
                this.jwtFilter = jwtFilter;
        }

        /**
         * Define as rotas personalizadas para o Gateway.
         * 
         * @param builder Construtor de rotas.
         * @return O RouteLocator configurado.
         */
        // @Bean
        // public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // return builder.routes()
        // .route("static-content", r -> r.path("/favicon.ico")
        // .uri("lb://static-content-service"))
        // // Rota principal para o serviço de usuário (legalconnect-usuario-service)
        // // Aplica o jwtFilter para interceptar e processar o token JWT.
        // .route("usuario-service", r -> r.path("/usuarios/**")
        // .filters(f -> f.filter(jwtFilter) // Aplica o filtro JWT aqui
        // .rewritePath("/usuarios/(?<segment>.*)",
        // "/${segment}"))
        // .uri("lb://legalconnect-usuario-service"))

        // // Rota para o Swagger UI
        // .route("swagger-ui", r -> r.path("/usuarios/swagger-ui/**")
        // .filters(f -> f.rewritePath(
        // "/usuarios/swagger-ui/(?<path>.*)",
        // "/swagger-ui/${path}"))
        // .uri("lb://legalconnect-usuario-service"))

        // // Rota para os recursos estáticos do Swagger (CSS, JS)
        // .route("swagger-resources", r -> r.path("/usuarios/webjars/**")
        // .filters(f -> f.rewritePath(
        // "/usuarios/webjars/(?<path>.*)",
        // "/webjars/${path}"))
        // .uri("lb://legalconnect-usuario-service"))

        // // Rota para a API Docs
        // .route("api-docs", r -> r.path("/usuarios/v3/api-docs/**")
        // .filters(f -> f.rewritePath(
        // "/usuarios/v3/api-docs/(?<path>.*)",
        // "/v3/api-docs/${path}"))
        // .uri("lb://legalconnect-usuario-service"))

        // .build();
        // }

}