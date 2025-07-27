package br.com.legalconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "br.com.legalconnect") // Este scanBasePackages já cobre tudo abaixo de
                                                                 // br.com.legalconnect
@EntityScan(basePackages = {
        "br.com.legalconnect.depoimento.domain.model", // Para a entidade Depoimento
        // Adicione outros pacotes de entidades aqui se você tiver mais módulos em
        // marketplace ou em br.com.legalconnect.entity
        "br.com.legalconnect.entity" // ADICIONADO: Para incluir entidades como Pessoa, User, etc.
})
@EnableJpaRepositories(basePackages = {
        "br.com.legalconnect.depoimento.infrastructure.repository", // Para DepoimentoJpaRepository
// Adicione outros pacotes de repositórios aqui
})
public class LegalMarketplaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalMarketplaceApplication.class, args);
    }
}
