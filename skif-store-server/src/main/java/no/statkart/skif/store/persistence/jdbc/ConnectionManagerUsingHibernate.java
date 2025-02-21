package no.statkart.skif.store.persistence.jdbc;

import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionManager;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionManager;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConnectionManager som oppretter connections via en tilknyttet {@link PersistenceSessionManager} og underliggende
 * {@link HibernatePersistenceSessionMaster}. Alle meoder for {@link no.statkart.skif.persistence.TransactionalResource}
 * på denne klasse metoder er tomme da de håndteres den tilknyttede {@code PersistenceSessionManager}. For å for dette
 * til settes {@code PersistenceSessionManager} til aktiv når connections hennes ut via denne klasse.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionManagerUsingHibernate implements ConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManagerUsingHibernate.class);
    private final PersistenceSessionManager persistenceSessionManager;

    final protected ConnectionProxyCache proxyCache = new ConnectionProxyCache();

    protected SnapshotVersion snapshotVersion;
    protected ConnectionForSnapshotVersion connection;


    public ConnectionManagerUsingHibernate(PersistenceSessionManager persistenceSessionManager) {
        this.persistenceSessionManager = persistenceSessionManager;
    }


    @Override
    public ConnectionForSnapshotVersion getForSnapshotVersion(SnapshotVersion snapshotVersion) {
        if (this.snapshotVersion == snapshotVersion) return connection;


        persistenceSessionManager.setActive();
        HibernatePersistenceSessionMaster implementation = persistenceSessionManager.getForSnapshotVersion(snapshotVersion).getImplementation(HibernatePersistenceSessionMaster.class);
        this.connection = proxyCache.getOrCreateProxy(implementation);
        this.snapshotVersion = snapshotVersion;
        return connection;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive() {
        // Ikke nødvendig å gjøre noe her, denne er alltid passiv
    }

    @Override
    public void close() {
        // Ikke nødvendig å gjøre noe her, håndteres av PersistenceSessionManager
    }

    @Override
    public void beginTransaction() {
        // Ikke nødvendig å gjøre noe her, håndteres av PersistenceSessionManager
    }

    @Override
    public void flush() {
        // Ikke nødvendig å gjøre noe her, håndteres av PersistenceSessionManager
    }

    @Override
    public void commit() {
        // Ikke nødvendig å gjøre noe her, håndteres av PersistenceSessionManager
    }

    @Override
    public void rollback() {
        // Ikke nødvendig å gjøre noe her, håndteres av PersistenceSessionManager
    }
}