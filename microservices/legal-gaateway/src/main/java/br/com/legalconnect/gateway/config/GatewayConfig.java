package br.com.legalconnect.gateway.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.legalconnect.gateway.filter.JwtAuthenticationGatewayFilter;
import br.com.legalconnect.gateway.util.JwtUtil;

/**
 * Classe de configuração para o Spring Cloud Gateway.
 * <p>
 * Define beans importantes para o funcionamento do Gateway, como o filtro de
 * autenticação JWT
 * e a configuração de rotas programáticas (embora as rotas principais estejam
 * no application.properties,
 * esta classe demonstra a capacidade programática e a aplicação global do
 * filtro).
 */
@Configuration
public class GatewayConfig {

    @Value("${jwt.public.paths}")
    private String publicPathsString;

    /**
     * Define o bean do filtro de autenticação JWT.
     * Este filtro será aplicado globalmente a todas as requisições que passarem
     * pelo Gateway.
     * A lógica de ignorar caminhos públicos está dentro do próprio filtro.
     *
     * @param jwtUtil Utilitário para manipulação de JWTs.
     * @return Uma instância de JwtAuthenticationGatewayFilter.
     */
    @Bean
    public JwtAuthenticationGatewayFilter jwtAuthenticationGatewayFilter(JwtUtil jwtUtil) {
        // Converte a string de caminhos públicos em uma lista.
        List<String> publicPaths = Arrays.asList(publicPathsString.split(","));
        return new JwtAuthenticationGatewayFilter(jwtUtil, publicPaths);
    }

    /**
     * Exemplo de configuração de rotas programáticas.
     * Embora as rotas principais sejam definidas no `application.properties`,
     * esta é uma alternativa/complemento para cenários mais complexos ou dinâmicos.
     * <p>
     * Nota: Ao definir rotas programaticamente e via `application.properties`,
     * ambas serão consideradas. Para evitar duplicidade ou comportamento
     * inesperado,
     * opte por um método principal de configuração de rotas.
     * Este bean está comentado para priorizar a configuração via
     * `application.properties`
     * conforme a solicitação, mas serve como exemplo de uso.
     *
     * @param builder O construtor de rotas fornecido pelo Spring Cloud Gateway.
     * @return Um {@link RouteLocator} que define as rotas do Gateway.
     */
    /*
     * @Bean
     * public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
     * JwtAuthenticationGatewayFilter jwtFilter) {
     * return builder.routes()
     * // Exemplo de rota para o auth-service (pode ser pública)
     * .route("auth_route", r -> r.path("/auth/**")
     * .filters(f -> f.filter(jwtFilter)) // Aplica o filtro de segurança (que
     * tratará caminhos públicos)
     * .uri("lb://AUTH-SERVICE"))
     * // Exemplo de rota para o user-service (pode ter caminhos públicos e
     * protegidos)
     * .route("user_route", r -> r.path("/users/**")
     * .filters(f -> f.filter(jwtFilter)) // Aplica o filtro de segurança
     * .uri("lb://USER-SERVICE"))
     * // Adicione mais rotas conforme necessário para outros microsserviços
     * .build();
     * }
     */
}