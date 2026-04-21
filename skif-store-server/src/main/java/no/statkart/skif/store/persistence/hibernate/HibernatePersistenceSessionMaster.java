package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.persistence.PersistenceSessionMaster;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

/**
 * @author Henrik Fredholm
 */
public interface HibernatePersistenceSessionMaster extends PersistenceSessionMaster {

    /**
     * TIP: Cast to {@link SharedSessionContractImplementor} to gain access to underlying connection.
     */
    Session reserveSession();

    void releaseSession();

}
