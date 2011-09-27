package no.statkart.skif.store2.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.ReplicaVersion2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProviderCurrent2 extends HibernateSessionProvider2 {
    @Inject
    public HibernateSessionProviderCurrent2(HibernateSessionManager2 hibernateSessionManager) {
        super(hibernateSessionManager, SnapshotVersion.CURRENT);
    }
}