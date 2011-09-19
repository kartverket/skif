package no.statkart.skif.store;

import com.google.inject.Inject;

/**
 * Strategi som mapper alle bobleobjekter til samme persister
 * @author Henrik Fredholm
 * @since 0.3
 */
public class SingleStorePersisterStrategy implements StorePersisterStrategy {
    final StorePersister persister;

    @Inject
    public SingleStorePersisterStrategy(StorePersister persister) {
        this.persister = persister;
    }

    @Override
    public StorePersister getPersister(BubbleId bubbleId) {
        return persister;
    }
}
