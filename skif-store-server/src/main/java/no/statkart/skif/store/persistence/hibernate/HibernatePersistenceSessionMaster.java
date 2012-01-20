package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.persistence.PersistenceSessionMaster;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
public interface HibernatePersistenceSessionMaster extends PersistenceSessionMaster {
    Session reserveSession();
    void releaseSession();
}
