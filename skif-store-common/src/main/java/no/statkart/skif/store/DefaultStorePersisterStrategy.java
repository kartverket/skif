package no.statkart.skif.store;

import java.util.LinkedHashMap;

/**
 * Default StorePersisterStrategy that is capable of managing several id-class to {@link StorePersister} bindings
 *
 * @author Leif Lislegård
 * @since 1.1
 */
public class DefaultStorePersisterStrategy implements StorePersisterStrategy {
    final LinkedHashMap<Class, StorePersister> persisters = new LinkedHashMap<Class, StorePersister>(4);


    public DefaultStorePersisterStrategy() {
    }

    /**
     * @param baseClass super id-class to tie persister to. If {@code null} then assigns this as the default persistor.
     * @param persister persistor for id-classes
     * @return this strategy
     */
    public DefaultStorePersisterStrategy addPersistor(StorePersister persister, Class baseClass) {
        persisters.put(baseClass, persister);
        return this;
    }

    /**
     * Default behaviour is to return the firstly added persistor that is compatible with the parameterized id.
     */
    @Override
    public StorePersister getPersister(AbstractBubbleId bubbleId) {
        Class persistorBaseClass = null;
        for (Class key : persisters.keySet()) {
            if (key != null && key.isAssignableFrom(bubbleId.getClass())) {
                persistorBaseClass = key;
                break; //firstly compatible added persistor takes precedence
            }
        }
        return persisters.get(persistorBaseClass);
    }
}

