package br.com.legalconnect.common.multitenancy;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider {

    private final DataSource dataSource;

    @Autowired
    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        log.info("Injetando DataSource: {}", dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        Connection connection = dataSource.getConnection();
        try {
            connection.setSchema((String) tenantIdentifier);
        } catch (SQLException e) {
            connection.close();
            throw new SQLException("Erro ao definir schema para tenant: " + tenantIdentifier, e);
        }
        return connection;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) this;
        }
        throw new IllegalArgumentException("Unwrapping não suportado para: " + unwrapType);
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        connection.close();
    }
}