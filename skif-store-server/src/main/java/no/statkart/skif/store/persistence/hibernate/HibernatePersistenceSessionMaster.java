package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.persistence.PersistenceSessionMaster;
import org.hibernate.internal.SessionImpl;

/**
 * @author Henrik Fredholm
 */
public interface HibernatePersistenceSessionMaster extends PersistenceSessionMaster {

    /**
     * Gir ut {@code SessionImpl} for å kunne få adgang til connection direkte.
     */
    SessionImpl reserveSession();

    void releaseSession();

}
