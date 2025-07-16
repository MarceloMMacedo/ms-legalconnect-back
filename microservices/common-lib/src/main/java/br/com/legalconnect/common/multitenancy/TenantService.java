package br.com.legalconnect.common.multitenancy;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;

    public TenantService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public void createTenantSchema(String tenantId) {
        // Verifica se o tenantId é válido (UUID)
        UUID.fromString(tenantId); // Lança exceção se não for válido

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + tenantId + "\"");
        jdbcTemplate.execute("SET search_path TO \"" + tenantId + "\"");

        // Aqui você pode executar migrações específicas para o novo tenant
        // Exemplo: jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (...)");
    }

    @Transactional
    public void initializeDefaultTenant() {
        if (defaultTenantId != null && !defaultTenantId.isEmpty()) {
            createTenantSchema(defaultTenantId);
        }
    }
}
