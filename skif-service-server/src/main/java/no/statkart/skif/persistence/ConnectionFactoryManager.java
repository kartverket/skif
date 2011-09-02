package no.statkart.skif.persistence;

/**
 * @author Henrik Fredholm
 */
public interface ConnectionFactoryManager {
    ConnectionFactory getFactory(Object key);
}
