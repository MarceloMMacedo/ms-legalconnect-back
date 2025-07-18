package br.com.legalconnect.bffpessoas.aplicacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class BffPessoasApplication
 * @brief Classe principal da aplicação Spring Boot para o microsserviço BFF de Pessoas.
 *
 * Esta classe inicializa o contexto da aplicação, habilita o escaneamento de componentes
 * e ativa a funcionalidade do OpenFeign para comunicação entre microsserviços.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "br.com.legalconnect.bffpessoas.client") // Habilita o Feign para o pacote de clients
public class BffPessoasApplication {

    private static final Logger log = LoggerFactory.getLogger(BffPessoasApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BffPessoasApplication.class, args);
        log.info("Microsserviço BFF de Pessoas iniciado com sucesso!");
    }
}