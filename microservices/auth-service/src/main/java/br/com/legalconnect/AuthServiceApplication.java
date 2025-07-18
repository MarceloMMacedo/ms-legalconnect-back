package br.com.legalconnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.context.annotation.ComponentScan; // REMOVA ESTA LINHA OU COMENTE-A
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@SpringBootApplication(exclude = FlywayAutoConfiguration.class) // Remova o scanBasePackages aqui também, se já está no
                                                                // pacote raiz
@EntityScan(basePackages = {
        "br.com.legalconnect.user.entity",
        "br.com.legalconnect.auth.entity"
})
@EnableJpaRepositories(basePackages = {
        "br.com.legalconnect.user.repository",
        "br.com.legalconnect.auth.repository"
})
// REMOVA COMPLETAMENTE ESTE @ComponentScan, a menos que você tenha um motivo
// muito específico e saiba o que está fazendo
// @ComponentScan(basePackages = { "br.com.legalconnect.auth.service" })
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        log.info("LegalConnectApplication iniciada com sucesso!");
    }
}