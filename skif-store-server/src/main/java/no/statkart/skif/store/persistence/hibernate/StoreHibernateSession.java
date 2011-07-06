package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.classic.Session;

import java.io.ObjectOutput;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreHibernateSession implements StoreSession {
    final Session session;
    final ReplicaVersion replicaVersion;

    public StoreHibernateSession(Session session, ReplicaVersion replicaVersion) {
        this.session = session;
        this.replicaVersion = replicaVersion;
    }

    public Session getHibernateSession() {
        return session;
    }
}
