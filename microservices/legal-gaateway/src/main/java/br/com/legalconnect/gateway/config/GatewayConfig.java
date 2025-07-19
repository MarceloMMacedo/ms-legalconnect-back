package br.com.legalconnect.gateway.config;

import java.net.URI;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    // Este bean é para permitir o roteamento dinâmico no application.yml
    // para o Swagger, onde precisamos pegar o URI de qualquer serviço.
    @Bean
    public CustomDiscoveryClientService customDiscoveryClientService(DiscoveryClient discoveryClient) {
        return new CustomDiscoveryClientService(discoveryClient);
    }

    public static class CustomDiscoveryClientService {
        private final DiscoveryClient discoveryClient;

        public CustomDiscoveryClientService(DiscoveryClient discoveryClient) {
            this.discoveryClient = discoveryClient;
        }

        public URI getServiceUri() {
            // Este método será usado no application.yml para resolver o URI dinamicamente.
            // Para o Swagger, precisamos de um URI genérico para que o Gateway possa
            // proxyar requisições para qualquer serviço.
            // Em um cenário real, você pode querer listar todos os serviços
            // e construir as rotas do Swagger dinamicamente em tempo de execução
            // ou ter um serviço de agregação de Swagger.
            // Aqui, apenas retornamos um URI base para o balanceador de carga.
            return URI.create("lb://");
        }
    }

}
