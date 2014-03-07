package no.statkart.skif.store.persistence.hibernate;

import java.util.List;

/**
 * @author Tor Egil R. Strand
 * @since 2.5.0 (som interface)
 */
public interface HibernateSessionFactoryManagerBundle {
    void close();

    List<HibernateSessionFactoryManager> getBundle();
}
