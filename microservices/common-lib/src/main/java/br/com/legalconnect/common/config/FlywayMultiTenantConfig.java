package br.com.legalconnect.common.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @class FlywayMultiTenantConfig
 * @brief Configuração do Flyway para suportar migrações de banco de dados em um
 *        ambiente multi-tenant.
 *
 *        Esta classe orquestra as migrações do Flyway: primeiro, migra o schema
 *        global (public)
 *        que contém a tabela de metadados dos tenants, e depois, para cada
 *        tenant registrado,
 *        cria seu schema dedicado (se não existir) e executa as migrações
 *        específicas do tenant.
 */
@Configuration
@Slf4j
public class FlywayMultiTenantConfig {

    @Autowired
    private DataSource dataSource; // O DataSource principal do Spring Boot

    @Value("${application.tenant.default-id}")
    private String defaultTenantId; // O ID do tenant padrão

    /**
     * @brief Define a estratégia de migração do Flyway para múltiplos tenants.
     *        Esta estratégia é executada após a migração do schema 'public'
     *        (global).
     *        Ela garante que APENAS o schema do tenant padrão seja migrado com os
     *        scripts de 'db/migration/tenants'.
     *        A migração de outros schemas de tenants existentes será feita por um
     *        ApplicationRunner.
     * @return Uma instância de FlywayMigrationStrategy.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            log.info("FlywayMultiTenantConfig: Iniciando a estratégia de migração multi-tenant para o DEFAULT TENANT.");

            // 1. Migrar o defaultTenantId (schema padrão do aplicativo)
            // Certifique-se que o schema defaultTenantId é criado antes de tentar migrar.
            // O método migrateSchema abaixo é responsável por isso.
            try {
                // Chama o helper para migrar apenas o schema do tenant padrão
                // Flyway.configure().schemas() tentará criar o schema se não existir.
                // Mas ter a criação explícita ajuda a evitar problemas de permissão.
                createSchemaIfNotExist(defaultTenantId, dataSource);

                Flyway defaultTenantFlyway = Flyway.configure()
                        .dataSource(dataSource)
                        .schemas(defaultTenantId) // O schema do tenant padrão
                        .locations("classpath:db/migration/tenants") // Localização dos scripts de tenant
                        .load();
                defaultTenantFlyway.migrate();
                log.info("FlywayMultiTenantConfig: Migração Flyway concluída para o schema padrão do aplicativo: {}",
                        defaultTenantId);
            } catch (Exception e) {
                log.error("FlywayMultiTenantConfig: Falha na migração Flyway para o schema padrão do aplicativo {}: {}",
                        defaultTenantId, e.getMessage(), e);
                // É crucial lançar uma RuntimeException aqui para falhar a inicialização se a
                // migração base falhar.
                throw new RuntimeException(
                        "Falha na migração Flyway para o schema padrão do aplicativo " + defaultTenantId, e);
            }
            log.info("FlywayMultiTenantConfig: Estratégia de migração para o default tenant concluída.");
        };
    }

    /**
     * Helper para garantir que um schema exista.
     * Adicionado para ser chamado explicitamente antes da migração do Flyway.
     */
    private void createSchemaIfNotExist(String schemaName, DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            String createSchemaSql = String.format("CREATE SCHEMA IF NOT EXISTS %s", schemaName);
            statement.execute(createSchemaSql);
            log.info("FlywayMultiTenantConfig: Schema '{}' verificado/criado com sucesso.", schemaName);
        }
    }
}