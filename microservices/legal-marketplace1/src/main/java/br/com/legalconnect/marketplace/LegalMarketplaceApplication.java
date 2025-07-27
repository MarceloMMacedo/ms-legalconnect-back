package br.com.legalconnect.marketplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "br.com.legalconnect.marketplace.depoimento.domain.model")
@EnableJpaRepositories(basePackages = "br.com.legalconnect.marketplace.depoimento.infrastructure.persistence")
public class LegalMarketplaceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LegalMarketplaceApplication.class, args);
	}
}
