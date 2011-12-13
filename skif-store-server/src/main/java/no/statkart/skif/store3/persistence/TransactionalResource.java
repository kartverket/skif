package no.statkart.skif.store3.persistence;

/**
 * @author Henrik Fredholm
 */
public interface TransactionalResource extends  ClosableResource{
    void beginTransaction();
    void commit();
    void rollback();
    void flush();
}
