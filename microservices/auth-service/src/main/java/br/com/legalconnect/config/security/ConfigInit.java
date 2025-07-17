package br.com.legalconnect.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import br.com.legalconnect.multitenancy.TenantContext;
import br.com.legalconnect.multitenancy.TenantSchemaService;

@Component
public class ConfigInit {

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;

    @Autowired
    private TenantSchemaService tenantService;

    @Bean
    public Boolean init() {
        if (defaultTenantId != null && !defaultTenantId.isEmpty()) {
            TenantContext.setCurrentTenant(defaultTenantId);

            try {
                tenantService.initializeDefaultTenant();
                return true;
            } finally {
                TenantContext.clear();
            }
        }
        return false;
    }
}
