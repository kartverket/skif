package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.store3.persistence.SnapshotManagedPersistenceSession;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
public class SnapshotManagedHibernatePersistenceSession implements SnapshotManagedPersistenceSession<HibernatePersistenceSession> {
    private final SnapshotManagedHibernateSession hibernateSessionManager;
    private final HibernateSessionDescriptor[] hibernateSessionDescriptors;
    private final HibernatePersistenceSessionDescriptor[] persistenceSessionDescriptors;

    public SnapshotManagedHibernatePersistenceSession(SnapshotManagedHibernateSession hibernateSessionManager) {
        this.hibernateSessionManager = hibernateSessionManager;
        this.hibernateSessionDescriptors = hibernateSessionManager.getPersistenceDescriptors();
        this.persistenceSessionDescriptors = new HibernatePersistenceSessionDescriptor[hibernateSessionDescriptors.length];
        for (int i = 0; i < hibernateSessionDescriptors.length; i++) {
            HibernateSessionDescriptor hibernateSessionDescriptor = hibernateSessionDescriptors[i];
            this.persistenceSessionDescriptors[i] = new HibernatePersistenceSessionDescriptor(hibernateSessionDescriptor);
        }
    }

    public SnapshotManagedHibernateSession getWrappedSessionManager() {
        return hibernateSessionManager;
    }

    @Override
    public HibernatePersistenceSession acquireForSnapshot(SnapshotVersion snapshotVersion) {
        Session session = hibernateSessionManager.acquireForSnapshot(snapshotVersion);
        int index = hibernateSessionManager.getPersistenceDescriptorIndex(session);
        HibernatePersistenceSessionDescriptor persistenceDescriptor = persistenceSessionDescriptors[index];
        HibernatePersistenceSession persistenceSession = persistenceDescriptor.getObject();
        // TODO: Vurderer en annen måte å håndtere close på. Skal man gjenbruke PersistenceSession eller skal man opprette en ny. Skal manageren bli fortalt at underliggende session har blitt lukket.
        if ( persistenceSession==null || persistenceSession.getWrappedSession()!=session) {
            HibernateSessionDescriptor sessionDescriptor = hibernateSessionDescriptors[index];
            persistenceSession = new HibernatePersistenceSession(sessionDescriptor.getObject(), sessionDescriptor.getEventSource(), sessionDescriptor.getSeed());
            persistenceDescriptor.setObject(persistenceSession);
        }
        return persistenceDescriptor.getObject();
    }

    @Override
    public HibernatePersistenceSession releaseForSnapshot(PersistenceSession persistenceSession) {
        if (persistenceSession==null) return null;
        for (int i = 0; i < persistenceSessionDescriptors.length; i++) {
            HibernatePersistenceSessionDescriptor persistenceSessionDescriptor = persistenceSessionDescriptors[i];
            if (persistenceSessionDescriptor.getObject()==persistenceSession) {
                hibernateSessionManager.releaseForSnapshot(persistenceSessionDescriptor.getWrapped().getObject());
                return null;
            }
        }
        throw new ImplementationException("Ukjent PersistenceSesison: " + persistenceSession);

    }

    @Override
    public HibernatePersistenceSessionDescriptor[] getPersistenceDescriptors() {
        return persistenceSessionDescriptors;

    }
}
