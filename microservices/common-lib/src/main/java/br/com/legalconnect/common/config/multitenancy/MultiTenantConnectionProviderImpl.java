package br.com.legalconnect.common.config.multitenancy;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * @class MultiTenantConnectionProviderImpl
 * @brief Implementação de MultiTenantConnectionProvider do Hibernate.
 *        Fornece a conexão de banco de dados para o schema do tenant atual.
 */
@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);

    private final DataSource dataSource;

    @Autowired
    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        log.debug("MultiTenantConnectionProviderImpl inicializado com DataSource.");
    }

    /**
     * Obtém uma conexão para o tenant especificado.
     * 
     * @return A conexão SQL para o tenant.
     * @throws SQLException Se ocorrer um erro ao obter a conexão.
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        log.trace("Obtendo qualquer conexão do DataSource.");
        return dataSource.getConnection();
    }

    /**
     * Libera a conexão para qualquer tenant.
     * 
     * @param connection A conexão a ser liberada.
     * @throws SQLException Se ocorrer um erro ao liberar a conexão.
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        log.trace("Liberando qualquer conexão.");
        connection.close();
    }

    /**
     * Obtém uma conexão para o tenant especificado.
     * Define o schema da conexão com base no tenantIdentifier.
     * 
     * @param tenantIdentifier O identificador do tenant (nome do schema).
     * @return A conexão SQL para o tenant.
     * @throws SQLException Se ocorrer um erro ao obter ou definir o schema da
     *                      conexão.
     */
    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        log.debug("Obtendo conexão para o tenant: {}", tenantIdentifier);
        final Connection connection = getAnyConnection();
        // Define o schema para a conexão. Isso é crucial para o multitenancy por
        // schema.
        try {
            connection.createStatement().execute("SET search_path to \"" + tenantIdentifier + "\", public");
            log.trace("Schema da conexão definido para: {}", tenantIdentifier);
        } catch (SQLException e) {
            log.error("Erro ao definir search_path para o tenant {}: {}", tenantIdentifier, e.getMessage(), e);
            throw e;
        }
        return connection;
    }

    /**
     * Libera a conexão para o tenant especificado.
     * 
     * @param tenantIdentifier O identificador do tenant.
     * @param connection       A conexão a ser liberada.
     * @throws SQLException Se ocorrer um erro ao liberar a conexão.
     */
    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        log.debug("Liberando conexão para o tenant: {}", tenantIdentifier);
        releaseAnyConnection(connection);
    }

    /**
     * Indica se este provedor de conexão suporta o conceito de "multi-tenancy".
     * 
     * @return True, pois suporta.
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    /**
     * Indica se a conexão atual é para o tenant especificado.
     * 
     * @param connection       A conexão a ser verificada.
     * @param tenantIdentifier O identificador do tenant.
     * @return True se a conexão pertence ao tenant, false caso contrário.
     */
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return unwrapType.isAssignableFrom(getClass());
    }

    /**
     * Verifica se o provedor de conexão é um proxy.
     * 
     * @return False, pois esta não é uma implementação de proxy.
     */
    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (unwrapType.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            throw new BusinessException(ErrorCode.INVALID_PROMO_CODE, unwrapType);
        }
    }
}