package br.com.legalconnect.gateway.config;

import java.net.URI;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração geral do Gateway.
 * Inclui um bean para permitir o roteamento dinâmico no application.yml,
 * especialmente útil para integração com o Swagger.
 */
@Configuration
public class GatewayConfig {

    /**
     * Este bean permite o roteamento dinâmico no application.yml para o Swagger,
     * onde precisamos pegar o URI de qualquer serviço.
     * 
     * @param discoveryClient Cliente de descoberta de serviços.
     * @return Instância de CustomDiscoveryClientService.
     */
    @Bean
    public CustomDiscoveryClientService customDiscoveryClientService(DiscoveryClient discoveryClient) {
        return new CustomDiscoveryClientService(discoveryClient);
    }

    /**
     * Serviço auxiliar para obter URI de serviços.
     */
    public static class CustomDiscoveryClientService {
        private final DiscoveryClient discoveryClient;

        public CustomDiscoveryClientService(DiscoveryClient discoveryClient) {
            this.discoveryClient = discoveryClient;
        }

        /**
         * Retorna um URI base para o balanceador de carga.
         * Em um cenário real, pode ser expandido para listar todos os serviços
         * e construir rotas dinâmicas para o Swagger.
         * 
         * @return URI base.
         */
        public URI getServiceUri() {
            return URI.create("lb://");
        }
    }

}