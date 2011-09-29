package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateSessionProviderCurrent extends HibernateSessionProvider {
    @Inject
    public HibernateSessionProviderCurrent(HibernateSessionManager hibernateSessionManager) {
        super(hibernateSessionManager, SnapshotVersion.CURRENT);
    }
}