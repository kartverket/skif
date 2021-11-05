package no.statkart.skif.service.module.server;

import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import no.statkart.skif.SkifModule;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.persistence.DefaultResourceManagerSpring;
import no.statkart.skif.persistence.ResourceManager;
import no.statkart.skif.persistence.jdbc.ConnectionFactoryUsingPoolSpring;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersionProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.persistence.jdbc.ConnectionManagerProvider;
import no.statkart.skif.persistence.jdbc.ConnectionManagerUsingFactory;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.store.SnapshotVersion;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ResourceWithSingleConnectionModuleSpring extends SkifModule {
    public ResourceWithSingleConnectionModuleSpring(ModuleConfiguration moduleConfiguration) {
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
    DataSource provideSpringDataSource() {
        Configuration configuration = moduleConfiguration.getConfiguration();
        ApplicationContext applicationContext = (ApplicationContext) configuration.getProperty("spring.application.context");
        return applicationContext.getBean(DataSource.class);
    }

    @Provides
    @ServiceRequestScoped
    ResourceManager provideResourceManager(Provider<DataSource> dataSourceProvider) {
        ConnectionManager connectionManager = new ConnectionManagerUsingFactory(
                new ConnectionFactoryUsingPoolSpring(dataSourceProvider.get(), false, SnapshotVersion.CURRENT, false)
        );
        return new DefaultResourceManagerSpring(
                new ResourceManager.Entry(connectionManager, ConnectionManager.class)
        );
    }
}
