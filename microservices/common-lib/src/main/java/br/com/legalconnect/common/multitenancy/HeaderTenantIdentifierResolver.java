package br.com.legalconnect.common.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeaderTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getTenantId();
        return (tenant != null && !tenant.trim().isEmpty()) ? tenant : defaultTenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}