package no.statkart.skif.store5.persistence;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionMaster extends PersistenceSessionForSnapshot {
    public void ensureBubblesFullyLoaded();
    public void close();
    public boolean hasLocalTrasaction();
    public void beginTransaction();
    public void commit();
    public void rollback();
    public void flush();
    public void clear();


}
