package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.store.persistence.StoreSession;

/**
 * Strategi som mapper alle bobleobjekter til samme persister
 * @author Henrik Fredholm
 * @since 0.3
 */
public class SingleStorePersisterStrategy implements StorePersisterStrategy {
    final StoreSession persister;

    @Inject
    public SingleStorePersisterStrategy(StoreSession persister) {
        this.persister = persister;
    }

    @Override
    public StoreSession getPersister(BubbleId bubbleId) {
        return persister;
    }
}
