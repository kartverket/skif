package no.statkart.skif.store.persistence;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionMaster extends PersistenceSessionForSnapshot {

    void ensureBubblesFullyLoaded();

    void close();

    boolean hasLocalTrasaction();

    void beginTransaction();

    void commit();

    void rollback();

    void flush();

    void clear();

    void verifySessionIsEmpty();

}
