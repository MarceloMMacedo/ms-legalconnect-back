package br.com.legalconnect.multitenancy;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantSchemaService {

    private static final Logger log = LoggerFactory.getLogger(TenantSchemaService.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;
    private final DataSource dataSource; // Adicionar DataSource para Flyway

    public TenantSchemaService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = null;
    }

    @Transactional
    public void createTenantSchema(String tenantId) {
        try {
            // UUID.fromString(tenantId); // Valida se é um UUID válido [cite: 514]
            String sanitizedSchema = (tenantId);

            log.info("Verificando/Criando schema '{}' para o tenant ID '{}'", sanitizedSchema, tenantId);
            // Cria o esquema se não existir
            jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS \"" + sanitizedSchema + "\"");
            log.info("Schema '{}' criado ou já existe.", sanitizedSchema);

            // Chamar o Flyway para migrar o novo schema
            runFlywayMigration(sanitizedSchema); // Chamada adicionada
            // IMPORTANTE: NÃO COLOQUE NENHUM CREATE TABLE AQUI para tabelas de tenant!
            // O JPA/Hibernate com ddl-auto=update fará isso automaticamente.

        } catch (IllegalArgumentException e) {
            log.error("Tenant ID inválido fornecido: {}", tenantId, e);
            throw new RuntimeException("Tenant ID inválido.", e);
        } catch (Exception e) {
            log.error("Erro ao criar schema para o tenant ID '{}'", tenantId, e);
            throw new RuntimeException("Erro ao provisionar schema para o tenant.", e);
        }
    }

    // Novo método para executar as migrações do Flyway para um schema específico
    private void runFlywayMigration(String schema) {
        log.info("Executando migrações Flyway para o schema: {}", schema);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schema) // Define o schema para o Flyway atuar
                .locations("db/migration") // Onde suas scripts de migração V*.sql estão
                .load();
        flyway.migrate(); // Executa as migrações
        log.info("Migrações Flyway concluídas para o schema: {}", schema);
    }

    // O método sanitizeSchemaName não é mais necessário aqui se o tenantId for um
    // UUID válido,
    // pois o UUID já é seguro para usar como nome de schema.
    // private String sanitizeSchemaName(String rawName) {
    // return rawName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
    // }

    @Transactional
    public void initializeDefaultTenant() {
        if (defaultTenantId != null && !defaultTenantId.isEmpty()) {
            log.info("Inicializando tenant padrão com ID: {}", defaultTenantId);
            createTenantSchema(defaultTenantId); // Apenas cria o schema [cite: 519]
            log.info("Schema para o tenant padrão inicializado.");
        }
    }

}