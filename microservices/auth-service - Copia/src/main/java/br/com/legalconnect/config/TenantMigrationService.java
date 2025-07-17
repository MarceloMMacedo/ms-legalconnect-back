package br.com.legalconnect.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.auth.repository.TenantRepository;
import br.com.legalconnect.config.multitenancy.TenantContext;
import jakarta.annotation.PostConstruct;

/**
 * @class TenantMigrationService
 * @brief Serviço responsável por gerenciar as migrações do Flyway para cada
 *        tenant.
 *        Garante que cada esquema de tenant tenha a versão mais recente do
 *        banco de dados.
 */
@Service
public class TenantMigrationService {

    private static final Logger log = LoggerFactory.getLogger(TenantMigrationService.class);

    private final DataSource dataSource;
    private final TenantRepository tenantRepository;

    @Value("${spring.flyway.locations}")
    private String[] flywayLocations; // Localização dos scripts de migração (ex: classpath:db/migration)

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean flywayBaselineOnMigrate;

    @Value("${spring.flyway.baseline-version}")
    private String flywayBaselineVersion;

    @Value("${application.tenant.default-id}") // Injeção da propriedade defaultTenantId
    private String defaultTenantId;

    public TenantMigrationService(DataSource dataSource, TenantRepository tenantRepository) {
        this.dataSource = dataSource;
        this.tenantRepository = tenantRepository;
    }

    /**
     * Executa as migrações do Flyway para todos os tenants existentes ao iniciar a
     * aplicação.
     * Marcado com @PostConstruct para ser executado após a injeção de dependências.
     */
    @PostConstruct
    public void migrateAllTenants() {
        log.info("Iniciando migrações do Flyway para todos os tenants existentes.");
        // Temporariamente define o TenantContext para 'public' para acessar a tabela
        // tb_tenant
        TenantContext.setCurrentTenant(defaultTenantId);
        try {
            List<Tenant> tenants = tenantRepository.findAll();
            for (Tenant tenant : tenants) {
                log.info("Migrando esquema para o tenant: {}", tenant.getSchemaName());
                migrateTenant(tenant.getSchemaName());
            }
            log.info("Migrações do Flyway para todos os tenants concluídas.");
        } catch (Exception e) {
            log.error("Erro ao executar migrações do Flyway para tenants: {}", e.getMessage(), e);
            // Dependendo da sua estratégia, você pode querer lançar a exceção ou apenas
            // logar.
            // Para garantir que a aplicação não inicie com um estado inconsistente, é bom
            // relançar.
            throw new RuntimeException("Falha ao migrar tenants na inicialização.", e);
        } finally {
            TenantContext.clear(); // Limpa o contexto do tenant
        }
    }

    /**
     * Executa as migrações do Flyway para um esquema de tenant específico.
     * Pode ser chamado ao provisionar um novo tenant.
     * 
     * @param schemaName O nome do esquema do tenant a ser migrado.
     */
    public void migrateTenant(String schemaName) {
        // Garante que o esquema exista antes de tentar migrar
        createSchemaIfNotExist(schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayLocations)
                .schemas(schemaName) // Define o esquema específico para esta migração
                .baselineOnMigrate(flywayBaselineOnMigrate)
                .baselineVersion(flywayBaselineVersion)
                .load();

        try {
            flyway.migrate();
            log.info("Migração do Flyway concluída com sucesso para o esquema: {}", schemaName);
        } catch (Exception e) {
            log.error("Erro ao executar migração do Flyway para o esquema {}: {}", schemaName, e.getMessage(), e);
            throw new RuntimeException("Falha na migração do esquema " + schemaName, e);
        }
    }

    /**
     * Cria o esquema no banco de dados se ele ainda não existir.
     * 
     * @param schemaName O nome do esquema a ser criado.
     */
    private void createSchemaIfNotExist(String schemaName) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            String sql = String.format("CREATE SCHEMA IF NOT EXISTS %s", schemaName);
            statement.execute(sql);
            log.debug("Esquema '{}' verificado/criado com sucesso.", schemaName);
        } catch (SQLException e) {
            log.error("Erro ao criar/verificar esquema '{}': {}", schemaName, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar/verificar esquema " + schemaName, e);
        }
    }
}