package no.statkart.skif.persistence;

import com.google.inject.Inject;

/**
 * @author Henrik Fredholm
 */
public class ConnectionFactoryManagerSingleVersionImpl implements ConnectionFactoryManager{
    private final ConnectionFactory factory;

    @Inject
    public ConnectionFactoryManagerSingleVersionImpl(ConnectionFactory factory) {
        this.factory = factory;
    }

    public ConnectionFactory getFactory(Object key) {
        return factory;
    }
}
