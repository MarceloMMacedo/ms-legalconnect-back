package br.com.legalconnect.common.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID; // Import UUID

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.config.multitenancy.TenantContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

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
    // private final TenantRepository tenantRepository;

    @Value("${spring.flyway.locations}")
    private String[] flywayLocations; // Localização dos scripts de migração (ex: classpath:db/migration)

    @Value("${spring.flyway.baseline-on-migrate}")
    private boolean flywayBaselineOnMigrate;

    @Value("${spring.flyway.baseline-version}")
    private String flywayBaselineVersion;

    @Value("${application.tenant.default-id}") // Injeção da propriedade defaultTenantId
    private String defaultTenantIdString; // Usar String para o valor da propriedade

    private UUID defaultTenantId; // Para armazenar o UUID parseado
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public TenantMigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
        // this.tenantRepository = tenantRepository;
    }

    @PostConstruct
    public void init() {
        this.defaultTenantId = UUID.fromString(defaultTenantIdString);
    }

    public void setarSchemaPadrao(String schemaName) {
        Map<String, Object> properties = entityManagerFactory.getProperties();
        properties.put("hibernate.default_schema", schemaName);
    }

    /**
     * Executa as migrações do Flyway para todos os tenants existentes ao iniciar a
     * aplicação.
     * Marcado com @PostConstruct para ser executado após a injeção de dependências.
     */
    @PostConstruct
    public void migrateAllTenantsOnStartup() { // Renomeado para clareza
        log.info("Iniciando migrações do Flyway para todos os tenants existentes.");
        // Temporariamente define o TenantContext para o schema do tenant padrão (onde
        // tb_tenant reside)
        // Isso é crucial para que o tenantRepository consiga acessar a tabela
        // tb_tenant.
        // Assumimos que o schema do defaultTenantId é o "public" ou o schema onde
        // tb_tenant está.
        // Se tb_tenant estiver em um schema diferente do defaultTenantId, isso precisa
        // ser ajustado.
        // Pelo seu SQL, tb_tenant não especifica schema, então estará no schema padrão
        // do DB (geralmente public).
        TenantContext.setCurrentTenant(defaultTenantId.toString()); // Ou o schema onde tb_tenant está.
        setarSchemaPadrao(defaultTenantId.toString());
        migrateTenant(defaultTenantId.toString());
        try {
            // List<Tenant> tenants = tenantRepository.findAll();
            // for (Tenant tenant : tenants) {
            // log.info("Migrando esquema para o tenant: {}", tenant.getSchemaName());
            // migrateTenant(tenant.getSchemaName());
            // }
            log.info("Migrações do Flyway para todos os tenants concluídas.");
        } catch (Exception e) {
            log.error("Erro ao executar migrações do Flyway para tenants: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao migrar tenants na inicialização.", e);
        }
        // finally {
        // TenantContext.clear(); // Limpa o contexto do tenant
        // }
    }

    public void migrate(String schemaName) {
        migrateTenant(schemaName);
        TenantContext.setCurrentTenant(schemaName); // Ou o schema onde tb_tenant está.
        setarSchemaPadrao(schemaName);
        migrateTenant(schemaName);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayLocations)
                .schemas(schemaName) // Define o esquema específico para esta migração
                .baselineOnMigrate(flywayBaselineOnMigrate)
                .baselineVersion(flywayBaselineVersion)
                .load();
        flyway.migrate();
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
        setarSchemaPadrao(schemaName);
        // Flyway flyway = Flyway.configure()
        // .dataSource(dataSource)
        // .locations(flywayLocations)
        // .schemas(schemaName) // Define o esquema específico para esta migração
        // .baselineOnMigrate(flywayBaselineOnMigrate)
        // .baselineVersion(flywayBaselineVersion)
        // .load();

        // try {
        // flyway.migrate();
        // log.info("Migração do Flyway concluída com sucesso para o esquema: {}",
        // schemaName);
        // } catch (FlywayValidateException ve) {
        // log.error("Validação do Flyway falhou para o schema {}: {}", schemaName,
        // ve.getMessage());

        // // Verifica se o erro foi por incompatibilidade de checksum e executa o
        // repair
        // if (ve.getMessage() != null && ve.getMessage().contains("Migration checksum
        // mismatch")) {
        // log.warn("Checksum mismatch detectado para o schema {}. Executando
        // Flyway.repair()...", schemaName);
        // try {
        // flyway.repair();
        // log.info("Flyway.repair() executado com sucesso. Tentando migrar novamente
        // para o schema: {}",
        // schemaName);
        // flyway.migrate();
        // } catch (Exception re) {
        // log.error("Falha ao executar Flyway.repair() ou nova tentativa de migrate
        // para o schema {}: {}",
        // schemaName, re.getMessage(), re);
        // throw new RuntimeException(
        // "Falha na migração mesmo após tentativa de repair para o schema " +
        // schemaName, re);
        // }
        // } else {
        // throw new RuntimeException("Erro de validação Flyway para o schema " +
        // schemaName, ve);
        // }
        // }
    }

    /**
     * Cria o esquema no banco de dados se ele ainda não existir.
     *
     * @param schemaName O nome do esquema a ser criado.
     */
    private void createSchemaIfNotExist(String schemaName) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            String sql = String.format("CREATE SCHEMA IF NOT EXISTS \"%s\"", schemaName);
            statement.execute(sql);
            TenantContext.setCurrentTenant(schemaName);
            log.debug("Esquema '{}' verificado/criado com sucesso.", schemaName);
        } catch (SQLException e) {
            log.error("Erro ao criar/verificar esquema '{}': {}", schemaName, e.getMessage(), e);
            throw new RuntimeException("Falha ao criar/verificar esquema " + schemaName, e);
        }
    }
}