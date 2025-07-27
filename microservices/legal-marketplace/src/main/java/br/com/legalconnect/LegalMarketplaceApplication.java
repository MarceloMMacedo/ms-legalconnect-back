package br.com.legalconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient

@SpringBootApplication(scanBasePackages = "br.com.legalconnect")
@EntityScan(basePackages = {
        "br.com.legalconnect.depoimento.domain.model"
})

@EnableJpaRepositories(basePackages = {
        "br.com.legalconnect.depoimento.infrastructure.persistence"
})
public class LegalMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalMarketplaceApplication.class, args);
    }
}
