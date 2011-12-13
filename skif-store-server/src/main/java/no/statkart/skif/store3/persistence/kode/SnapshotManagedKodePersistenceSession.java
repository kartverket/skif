package no.statkart.skif.store3.persistence.kode;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store3.persistence.PersistenceSession;
import no.statkart.skif.store3.persistence.SnapshotManagedPersistenceSession;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionDescriptor;
import no.statkart.skif.store3.persistence.hibernate.SnapshotManagedHibernateSession;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
public class SnapshotManagedKodePersistenceSession implements SnapshotManagedPersistenceSession<KodePersistenceSession> {
    private final SnapshotManagedHibernateSession hibernateSessionManager;
    private final HibernateSessionDescriptor[] hibernateSessionDescriptors;
    private final KodePersistenceSessionDescriptor[] persistenceSessionDescriptors;

    public SnapshotManagedKodePersistenceSession(SnapshotManagedHibernateSession hibernateSessionManager) {
        this.hibernateSessionManager = hibernateSessionManager;
        this.hibernateSessionDescriptors = hibernateSessionManager.getPersistenceDescriptors();
        this.persistenceSessionDescriptors = new KodePersistenceSessionDescriptor[hibernateSessionDescriptors.length];
        for (int i = 0; i < hibernateSessionDescriptors.length; i++) {
            HibernateSessionDescriptor hibernateSessionDescriptor = hibernateSessionDescriptors[i];
            this.persistenceSessionDescriptors[i] = new KodePersistenceSessionDescriptor(hibernateSessionDescriptor);
        }
    }

    @Override
    public KodePersistenceSession acquireForSnapshot(SnapshotVersion snapshotVersion) {
        Session session = hibernateSessionManager.acquireForSnapshot(snapshotVersion);
        int index = hibernateSessionManager.getPersistenceDescriptorIndex(session);
        KodePersistenceSessionDescriptor persistenceDescriptor = persistenceSessionDescriptors[index];
        KodePersistenceSession persistenceSession = persistenceDescriptor.getObject();
        if ( persistenceSession==null) {
            HibernateSessionDescriptor sessionDescriptor = hibernateSessionDescriptors[index];
            persistenceSession = new KodePersistenceSession(sessionDescriptor.getObject(), sessionDescriptor.getEventSource(), sessionDescriptor.getSeed());
            persistenceDescriptor.setObject(persistenceSession);
        }
        return persistenceDescriptor.getObject();
    }

    @Override
    public KodePersistenceSession releaseForSnapshot(PersistenceSession persistenceSession) {
        if (persistenceSession==null) return null;

        for (int i = 0; i < persistenceSessionDescriptors.length; i++) {
            KodePersistenceSessionDescriptor persistenceSessionDescriptor = persistenceSessionDescriptors[i];
            if (persistenceSessionDescriptor.getObject()==persistenceSession) {
                hibernateSessionManager.releaseForSnapshot(persistenceSessionDescriptor.getWrapped().getObject());
                return null;
            }
        }
        throw new ImplementationException("Ukjent PersistenceSesison: " + persistenceSession);
    }

    @Override
    public KodePersistenceSessionDescriptor[] getPersistenceDescriptors() {
        return persistenceSessionDescriptors;

    }
}
