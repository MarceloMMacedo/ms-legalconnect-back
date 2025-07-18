package br.com.legalconnect.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import br.com.legalconnect.common.config.multitenancy.CurrentTenantIdentifierResolverImpl;
import br.com.legalconnect.common.config.multitenancy.MultiTenantConnectionProviderImpl;

/**
 * @class HibernateConfig
 * @brief Configuração do Hibernate para suporte a multitenancy.
 *        Define como o Hibernate interage com o banco de dados em um ambiente
 *        multi-tenant por schema.
 */
@Configuration
public class HibernateConfig {

    private static final Logger log = LoggerFactory.getLogger(HibernateConfig.class);

    private final JpaProperties jpaProperties;
    private final DataSource dataSource;
    private final MultiTenantConnectionProviderImpl multiTenantConnectionProvider;
    private final CurrentTenantIdentifierResolverImpl currentTenantIdentifierResolver;

    @Autowired
    public HibernateConfig(
            JpaProperties jpaProperties,
            DataSource dataSource,
            MultiTenantConnectionProviderImpl multiTenantConnectionProvider,
            CurrentTenantIdentifierResolverImpl currentTenantIdentifierResolver) {
        this.jpaProperties = jpaProperties;
        this.dataSource = dataSource;
        this.multiTenantConnectionProvider = multiTenantConnectionProvider;
        this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
        log.debug("HibernateConfig inicializado.");
    }

    /**
     * Configura o JpaVendorAdapter para Hibernate.
     * 
     * @return O JpaVendorAdapter configurado.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        log.debug("Configurando JpaVendorAdapter.");
        return new HibernateJpaVendorAdapter();
    }

    /**
     * Configura o EntityManagerFactory para suportar multitenancy.
     * 
     * @return O LocalContainerEntityManagerFactoryBean configurado.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        log.info("Configurando EntityManagerFactory para multitenancy de schema.");
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(
                "br.com.legalconnect.user.entity",
                "br.com.legalconnect.auth.entity"); // Pacotes onde suas entidades estão localizadas
        em.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.multiTenancy", "SCHEMA");
        properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        properties.put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        em.setJpaPropertyMap(properties);
        log.debug("EntityManagerFactory configurado com propriedades de multitenancy: {}", properties);
        return em;
    }
}