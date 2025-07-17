package br.com.legalconnect.multitenancy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigInit {

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;

    @Autowired
    private TenantSchemaService tenantService;

    // Remover @Bean daqui
    public void init() {
        if (defaultTenantId != null && !defaultTenantId.isEmpty()) {
            TenantContext.setCurrentTenant(defaultTenantId);
            try {
                tenantService.initializeDefaultTenant();
            } finally {
                TenantContext.clear();
            }
        }
    }
}
