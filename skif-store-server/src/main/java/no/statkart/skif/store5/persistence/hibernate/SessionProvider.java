package no.statkart.skif.store5.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SessionProvider implements Provider<Session> {
    private final HibernatePersistenceSessionMaster master;

    @Inject
    public SessionProvider(PersistenceSessionManager manager) {
        this.master = manager.getForSnapshotVersion(SnapshotVersion.CURRENT).getImplementation(HibernatePersistenceSessionMaster.class);
    }

    @Override
    public Session get() {
        return master.reserveSession();
    }
}
