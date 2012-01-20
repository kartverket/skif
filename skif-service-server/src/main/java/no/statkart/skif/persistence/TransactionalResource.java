package no.statkart.skif.persistence;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface TransactionalResource extends Resource {
    void beginTransaction();
    void flush();
    void commit();
    void rollback();
}
