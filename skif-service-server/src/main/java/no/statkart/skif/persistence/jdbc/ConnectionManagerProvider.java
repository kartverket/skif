package no.statkart.skif.persistence.jdbc;


import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.ResourceManager;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionManagerProvider implements Provider<ConnectionManager> {
    private final ResourceManager resourceManager;

    @Inject
    public ConnectionManagerProvider(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public ConnectionManager get() {
        return resourceManager.getResource(ConnectionManager.class);
    }
}
