package no.statkart.skif.service.module.server;

import com.google.inject.Provides;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.*;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;

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
    @ServiceRequestScoped
    ResourceManager provideResourceManager() {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ConnectionManager connectionManager;
        if (moduleConfiguration.getServiceMode() == ServiceMode.SINGLE_VM) {
            String username = configuration.getString(SkifConfigConstants.DB_USERNAME);
            String password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
            String sid = configuration.getString(SkifConfigConstants.DB_SID);
            String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
            String port = configuration.getString(SkifConfigConstants.DB_PORT);
            String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);

            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingJDBC(url, username, password, false, SnapshotVersion.CURRENT, false)
            );
        } else {
            String datasource = configuration.getString(SkifConfigConstants.DB_DATASOURCE, "no.statkart.matrikkel.persistens.MatrikkelBok_DS");
            connectionManager = new ConnectionManagerUsingFactory(
                    new ConnectionFactoryUsingDataSource(datasource, false, SnapshotVersion.CURRENT, false)
            );
        }

        ResourceManager resourceManager = new ResourceManager(
                new ResourceManager.Entry(connectionManager, ConnectionManager.class)
        );
        return resourceManager;
    }
}
