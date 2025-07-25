package br.com.legalconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {
		"br.com.legalconnect.advogado.domain.repository",
		"br.com.legalconnect.commom.repository" })
@EntityScan(basePackages = { "br.com.legalconnect.commom.model", "br.com.legalconnect.advogado.domain.modal.entity" })
@EnableDiscoveryClient
@SpringBootApplication
public class LegalUsuarioApplication {
	// @Value("${application.tenant.default-id}")
	// private String defaultTenantId;

	// @Autowired
	// private TenantMigrationService tenantMigrationService;

	public static void main(String[] args) {
		SpringApplication.run(LegalUsuarioApplication.class, args);
	}
}
// @Bean
// boolean inicio() {
// tenantMigrationService.migrateTenant(defaultTenantId);
// return true;
// }
// }
