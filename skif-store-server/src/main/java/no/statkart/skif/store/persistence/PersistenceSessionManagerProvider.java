package no.statkart.skif.store.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.ResourceManager;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class PersistenceSessionManagerProvider implements Provider<PersistenceSessionManager> {
    private final ResourceManager resourceManager;

    @Inject
    public PersistenceSessionManagerProvider(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public PersistenceSessionManager get() {
        return resourceManager.getResource(PersistenceSessionManager.class);
    }
}
