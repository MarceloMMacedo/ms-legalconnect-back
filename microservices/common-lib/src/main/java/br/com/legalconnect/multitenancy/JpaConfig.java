package br.com.legalconnect.multitenancy;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class JpaConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false); // Desative se estiver usando Flyway/Liquibase
        return vendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver tenantIdentifierResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                "br.com.legalconnect.auth.entity",
                "br.com.legalconnect.user.entity",
                "br.com.legalconnect.common.entity");
        em.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.putAll(jpaProperties.getProperties());

        // Configurações alternativas para multitenancy
        properties.put("hibernate.multiTenancy", "SCHEMA");
        properties.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);
        properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);

        // Configurações para PostgreSQL e criação de tabelas
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Estratégia de atualização do schema
        // properties.put("hibernate.hbm2ddl.auto", "create"); // Ou "validate" em
        // produção

        // Configurações adicionais recomendadas
        // properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        // properties.put("hibernate.jdbc.lob.non_contextual_creation", "true");

        em.setJpaPropertyMap(properties);
        return em;
    }
}