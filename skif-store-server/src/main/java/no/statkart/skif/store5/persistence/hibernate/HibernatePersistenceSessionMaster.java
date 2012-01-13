package no.statkart.skif.store5.persistence.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store5.persistence.PersistenceSessionMaster;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
public interface HibernatePersistenceSessionMaster extends PersistenceSessionMaster {
    Session reserveSession();
    void releaseSession();
}
