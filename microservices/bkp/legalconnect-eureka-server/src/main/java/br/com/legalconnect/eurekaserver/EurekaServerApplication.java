package br.com.legalconnect.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Classe principal da aplicação Spring Boot para o Eureka Server.
 * <p>
 * Anotada com {@code @SpringBootApplication} para habilitar a configuração
 * automática do Spring Boot,
 * varredura de componentes e configurações de beans.
 * <p>
 * Anotada com {@code @EnableEurekaServer} para habilitar este microsserviço
 * como um servidor de descoberta
 * de serviços Eureka. Ele será responsável por registrar e fornecer informações
 * sobre outros microsserviços
 * que se conectarem a ele.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot do Eureka Server.
     *
     * @param args Argumentos de linha de comando passados para a aplicação.
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}
