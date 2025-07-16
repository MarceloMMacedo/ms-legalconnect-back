package br.com.legalconnect.common.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)
public class ConfigInit {

    @Autowired
    private TenantService tenantService;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            // Cria o schema do tenant padrão
            tenantService.initializeDefaultTenant();

            // Exemplo: criar outros tenants se necessário
            // tenantService.createTenantSchema("outro-tenant-id");
        };
    }
}
