package no.statkart.skif.store.persistence.hibernate;


import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.ConnectionFactoryManager;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionHolder;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Hibernate basert {@code StoreSessionManager} med støtte for {@code SnapshotVersion}
 *
 * @author Henrik Fredholm
 */
public class HibernateStoreSessionManagerSnapshotVersionImpl extends AbstractHibernateSessionManager<HibernateStoreSessionManagerSnapshotVersionEntry> implements HibernateStoreSessionManager {
    private static Logger logger = LoggerFactory.getLogger(HibernateStoreSessionManagerSnapshotVersionImpl.class);
    private final ServiceRequestContext serviceRequestContext;
    private HibernateStoreSessionManagerSnapshotVersionEntry[] entries = new HibernateStoreSessionManagerSnapshotVersionEntry[2];
    private final Deque<SnapshotVersion> snapshotVersionScope = new ArrayDeque<SnapshotVersion>();
    private final int UPDATABLE = 0;
    private final int BEFORE_UPDATE = 1;


    @Inject
    public HibernateStoreSessionManagerSnapshotVersionImpl(ConnectionFactoryManager connectionFactoryManager, HibernateSessionFactoryManagerSnapshotVersionImpl hibernateSessionFactoryManager, ServiceRequestContext serviceRequestContext) {
        super(connectionFactoryManager, hibernateSessionFactoryManager);
        this.serviceRequestContext = serviceRequestContext;
        Object[] keys = hibernateSessionFactoryManager.getKeys();

        entries[UPDATABLE] = new HibernateStoreSessionManagerSnapshotVersionEntry((SnapshotVersionHolder)keys[0]);
        entries[BEFORE_UPDATE] = new HibernateStoreSessionManagerSnapshotVersionEntry((SnapshotVersionHolder)keys[1]);
    }

    @Override
    protected HibernateStoreSessionManagerSnapshotVersionEntry getEntry(Object key) {
        if (SnapshotVersion.CURRENT==key || SnapshotVersion.NOT_VERSIONED==key) {
            return entries[UPDATABLE];
        } if (SnapshotVersion.OLD == key) {
            return entries[BEFORE_UPDATE];
        } else {
            throw new ImplementationException("Metode getEntry støtter ikke bruk av snapshot= " + key);
        }
    }

    @Override
    public void closeHibernateSession(HibernateStoreSessionManagerSnapshotVersionEntry entry) {
        super.closeHibernateSession(entry);
        entry.storeSession = null;
    }

    @Override
    public void openHibernateSession(HibernateStoreSessionManagerSnapshotVersionEntry entry) {
        super.openHibernateSession(entry);
        entry.storeSession = new HibernateStoreSession(entry.session, (SnapshotVersionHolder) entry.key);
    }

    @Override
    public void flush() {
        flushEntry(entries[UPDATABLE]);
        flushEntry(entries[BEFORE_UPDATE]);
    }

    @Override
    public void close() {
        closeEntry(entries[UPDATABLE]);
        closeEntry(entries[BEFORE_UPDATE]);

    }

    @Override
    public void beginTransaction() {
        HibernateStoreSessionManagerSnapshotVersionEntry entry = entries[UPDATABLE];
        if (entry.useLocalTransaction) {
            throw new ImplementationException("Transaction has already been started");
        }
        if (entry.session != null) {
            entry.hibernateTransaction = entry.session.beginTransaction();
        }
        entry.useLocalTransaction = true;
    }

    @Override
    public void commit() {
        HibernateStoreSessionManagerSnapshotVersionEntry entry = entries[UPDATABLE];
        commitEntry(entry);
    }

    @Override
    public void rollback() {
        HibernateStoreSessionManagerSnapshotVersionEntry entry = entries[UPDATABLE];
        rollbackEntry(entry);
    }

    @Override
    public HibernateStoreSession getStoreSession(SnapshotVersion snapshotVersion) {
        HibernateStoreSessionManagerSnapshotVersionEntry entry = getEntry(snapshotVersion);
        if (entry.storeSession == null) {
            getHibernateSessionEntry(entry);
            entry.storeSession = createHibernateStoreSession(entry.session, entry.key);
        }
        return entry.storeSession;
    }

    protected HibernateStoreSession createHibernateStoreSession(Session session, Object key) {
        return new HibernateStoreSession(session, (SnapshotVersionHolder) key);
    }

    @Override
    public void beginSnapshotScope(SnapshotVersion snapshotVersion) {
        snapshotVersionScope.push(snapshotVersion);
    }

    @Override
    public void endSnapshotScope() {
        snapshotVersionScope.pop();
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSessionUsingSnapshotScope() {
        SnapshotVersion scope = snapshotVersionScope.peek();
        if (scope==null) {
            throw new ImplementationException("SnapshotScope er ikke satt");
        }
        return acquireSnapshotStoreSession(scope);
    }

    @Override
    public HibernateStoreSession acquireSnapshotStoreSession(SnapshotVersion snapshotVersion) {
        HibernateStoreSession result;
        if (hasSnapshotVersion(entries[UPDATABLE], snapshotVersion)) {
            // Bruk eksisterende UPDATABLE
            result = pushExistingSnapshotVersion(entries[UPDATABLE]);
        } else if (hasSnapshotVersion(entries[BEFORE_UPDATE], snapshotVersion)) {
            // Bruk eksisterende BEFORE_UPDATE
            result = pushExistingSnapshotVersion(entries[BEFORE_UPDATE]);
        } else {
            if (SnapshotVersion.CURRENT == snapshotVersion || SnapshotVersion.NOT_VERSIONED == snapshotVersion) {
                // Bruk default for CURRENT og NOT_VERSIONED
                result = pushSnapshotVersion(entries[UPDATABLE], snapshotVersion);
            } else if (SnapshotVersion.OLD == snapshotVersion) {
                // Bruk default for OLD
                result = pushSnapshotVersion(entries[BEFORE_UPDATE],snapshotVersion);
            } else if (entries[UPDATABLE].storeSession == null) {
                // Bruk ledig
                result = pushSnapshotVersion(entries[UPDATABLE], snapshotVersion);
            } else if (entries[BEFORE_UPDATE].storeSession == null) {
                // Bruk ledig
                result = pushSnapshotVersion(entries[BEFORE_UPDATE], snapshotVersion);
            } else {
                // Ingen ledig og snapshotVersion er historisk. Sjeck scope og bruk UPDATABLE hvis match med scope
                if (snapshotVersion.equals(snapshotVersionScope.peek())) {
                    result = pushSnapshotVersion(entries[UPDATABLE], snapshotVersion);
                } else {
                    result = pushSnapshotVersion(entries[BEFORE_UPDATE], snapshotVersion);
                }
            }
        }
        return result;
    }


    @Override
    public void releaseSnapshotStoreSession(StoreSession storeSession) {
        if (entries[UPDATABLE].storeSession == storeSession) {
            popSnapshotVersion(entries[UPDATABLE]);
        } else if (entries[BEFORE_UPDATE].storeSession == storeSession) {
            popSnapshotVersion(entries[BEFORE_UPDATE]);
        } else {
            throw new ImplementationException("Forsøk på å frigi StoreSession instans som ikke er i bruk");
        }
    }


    private HibernateStoreSession pushExistingSnapshotVersion(HibernateStoreSessionManagerSnapshotVersionEntry entry) {
        entry.snapshotVersionStack.push(entry.snapshotVersionStack.peek());
        return entry.storeSession;
    }

    private boolean hasSnapshotVersion(HibernateStoreSessionManagerSnapshotVersionEntry entry, SnapshotVersion snapshotVersion) {
        return entry.session !=null && entry.snapshotVersionStack.peek().equals(snapshotVersion);
    }

    private HibernateStoreSession pushSnapshotVersion(HibernateStoreSessionManagerSnapshotVersionEntry entry, SnapshotVersion snapshotVersion) {
        if (entry.storeSession==null) {
            openHibernateSession(entry);
        } else {
            entry.storeSession.ensureBubblesFullyLoaded();
            entry.storeSession.evictAll();
        }
        entry.pushSnapshotVersion(entry.getSnapshotVersion());
        entry.setSnapshotVersion(snapshotVersion);
        return entry.storeSession;
    }

    private void popSnapshotVersion(HibernateStoreSessionManagerSnapshotVersionEntry entry) {
        SnapshotVersion s = entry.getSnapshotVersion();
        if (!s.equals(entry.snapshotVersionStack.peek())) {
            entry.storeSession.ensureBubblesFullyLoaded();
            entry.storeSession.evictAll();
        }
        SnapshotVersion snapshotVersion = entry.snapshotVersionStack.pop();
        entry.setSnapshotVersion(snapshotVersion);
    }
}
