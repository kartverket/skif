package no.statkart.skif.persistence;

import com.google.inject.Inject;

import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class ConnectionFactoryManagerMultiVersionImpl implements ConnectionFactoryManager{
    private final Map<Object,ConnectionFactory> factoryMap;

    @Inject
    public ConnectionFactoryManagerMultiVersionImpl(Map<Object,ConnectionFactory> factoryMap) {
        this.factoryMap = factoryMap;
    }

    public ConnectionFactory getFactory(Object key) {
        return factoryMap.get(key);
    }
}
