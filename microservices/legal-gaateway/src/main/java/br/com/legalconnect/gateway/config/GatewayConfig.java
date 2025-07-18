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
 * Responsável por definir beans importantes para o funcionamento do Gateway,
 * como o filtro de autenticação JWT e, opcionalmente, a configuração de rotas
 * programáticas.
 */
@Configuration
public class GatewayConfig {

    /**
     * Injeta a string de caminhos públicos do arquivo application.properties.
     * Estes caminhos não exigirão validação de JWT.
     */
    @Value("${jwt.public.paths}")
    private String publicPathsString;

    /**
     * Define o bean do filtro de autenticação JWT.
     * <p>
     * Este filtro será aplicado a todas as requisições que passarem pelo Gateway.
     * A lógica interna do filtro determinará se a requisição é para um caminho
     * público
     * e, portanto, deve ignorar a validação de JWT.
     *
     * @param jwtUtil Utilitário para manipulação e validação de JWTs.
     * @return Uma instância de {@link JwtAuthenticationGatewayFilter}.
     */
    @Bean
    public JwtAuthenticationGatewayFilter jwtAuthenticationGatewayFilter(JwtUtil jwtUtil) {
        // Converte a string de caminhos públicos (separados por vírgula) em uma lista.
        List<String> publicPaths = Arrays.asList(publicPathsString.split(","));
        return new JwtAuthenticationGatewayFilter(jwtUtil, publicPaths);
    }

    /**
     * Exemplo de configuração de rotas programáticas.
     * <p>
     * Embora as rotas principais para este projeto sejam definidas no
     * `application.properties`
     * (conforme solicitado e é a abordagem mais comum para rotas estáticas),
     * este método demonstra como as rotas poderiam ser configuradas
     * programaticamente
     * usando
     * {@link org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder}.
     * <p>
     * Este bean está comentado para priorizar a configuração via
     * `application.properties`.
     * Se descomentado, as rotas definidas aqui seriam adicionadas às rotas do
     * `application.properties`.
     * Para evitar duplicação ou comportamento inesperado, é recomendável escolher
     * um método principal
     * de configuração de rotas (propriedades ou código).
     *
     * @param builder   O construtor de rotas fornecido pelo Spring Cloud Gateway.
     * @param jwtFilter O filtro JWT, que será aplicado a todas as rotas definidas
     *                  aqui.
     * @return Um {@link org.springframework.cloud.gateway.route.RouteLocator} que
     *         define as rotas do Gateway.
     */
    /*
     * @Bean
     * public RouteLocator customRouteLocator(RouteLocatorBuilder builder,
     * JwtAuthenticationGatewayFilter jwtFilter) {
     * return builder.routes()
     * // Rota para o serviço de autenticação
     * .route("auth_service_route", r -> r.path("/auth/**")
     * .filters(f -> f.filter(jwtFilter)) // Aplica o filtro de segurança
     * .uri("lb://AUTH-SERVICE")) // lb:// indica balanceamento de carga via Eureka
     * // Rota para o serviço de usuário
     * .route("user_service_route", r -> r.path("/users/**")
     * .filters(f -> f.filter(jwtFilter)) // Aplica o filtro de segurança
     * .uri("lb://USER-SERVICE"))
     * // Adicione mais rotas para outros microsserviços conforme necessário
     * .build();
     * }
     */
}