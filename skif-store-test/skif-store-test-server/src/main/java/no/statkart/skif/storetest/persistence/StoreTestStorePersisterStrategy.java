package no.statkart.skif.storetest.persistence;


import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.HashStorePersister;
import no.statkart.skif.store.StorePersister;
import no.statkart.skif.store.StorePersisterStrategy;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.store.kodelistesupport.KodelisteImplId;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionPersister;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;

/**
 * Strategi som mapper bobleobjekter til hibernate-persister, mens hjelpemodellobjekter blir mappet til egen persister
 * @author Henrik Fredholm
 * @since 0.6
 *
 */
public class StoreTestStorePersisterStrategy implements StorePersisterStrategy {
    final HibernateStoreSessionPersister persister1;
    final HashStorePersister persister2;
    final KodelistePersister kodelistePersister;

    public StoreTestStorePersisterStrategy(HibernateStoreSessionPersister persister1, HashStorePersister persister2, KodelistePersister kodelistePersister) {
        this.persister1 = persister1;
        this.persister2 = persister2;
        this.kodelistePersister = kodelistePersister;
    }

    @Override
    public StorePersister getPersister(BubbleId bubbleId) {
        if (bubbleId instanceof KodeId) {
            return kodelistePersister;
        } else if(bubbleId instanceof KodelisteImplId) {
            return kodelistePersister;
        } else {
            return persister1;
        }
    }
}