package no.statkart.skif.service.module.server;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.persistence.DefaultResourceManager;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.*;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ResourceWithSingleConnectionModule extends SkifModule {
    public ResourceWithSingleConnectionModule(ModuleConfiguration moduleConfiguration) {
        super(moduleConfiguration);
    }

    @Override
    protected void configure() {
        bind(ConnectionManager.class).toProvider(ConnectionManagerProvider.class);
        bind(Connection.class).to(ConnectionForSnapshotVersion.class);
        bind(ConnectionForSnapshotVersion.class).toProvider(ConnectionForSnapshotVersionProvider.class);

    }

    @Provides
    @Singleton
    ComboPooledDataSource provideConnectionPool() {
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM || moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM_XML) {
            Configuration configuration = moduleConfiguration.getConfiguration();
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String service = configuration.getString(SkifConfigConstants.DB_SERVICE);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@//%s:%s/%s", hostname, port, service);

            try {
                ComboPooledDataSource pool = new ComboPooledDataSource();
                pool.setDriverClass("oracle.jdbc.OracleDriver");
                pool.setJdbcUrl(url);
                pool.setUser(username);
                pool.setPassword(password);
                return pool;
            } catch (PropertyVetoException e) {
                throw new ImplementationException("Could not set up connection pool", e);
            }
        } else {
            throw new ImplementationException("Will not provide connection pool in JEE-mode. Get datasource from JNDI.");
        }
    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(Provider<ComboPooledDataSource> dataSourceProvider) {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ConnectionManager connectionManager;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM || moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM_XML) {
            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingPool(dataSourceProvider.get(), false, SnapshotVersion.CURRENT, false)
            );
        } else {
            String datasource = configuration.getString(SkifConfigConstants.DB_DATASOURCE);
            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingDataSource(datasource, false, SnapshotVersion.CURRENT, false)
            );
        }

        ResourceManager resourceManager = new DefaultResourceManager(
                new ResourceManager.Entry(connectionManager, ConnectionManager.class)
        );
        return resourceManager;
    }
}
