package no.statkart.skif.storetest.persistence2;


import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.HashStorePersister2;
import no.statkart.skif.store2.StorePersister2;
import no.statkart.skif.store2.StorePersisterStrategy2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;
import no.statkart.skif.store2.persistence.hibernate.HibernateStoreSession2;
import no.statkart.skif.store2.persistence.kodeliste.KodelistePersister2;

/**
 * Strategi som mapper bobleobjekter til hibernate-persister, mens hjelpemodellobjekter blir mappet til egen persister
 * @author Henrik Fredholm
 * @since 0.6
 *
 */
public class StoreTestStorePersisterStrategy2 implements StorePersisterStrategy2 {
    final HibernateStoreSession2 persister1;
    final HashStorePersister2 persister2;
    final KodelistePersister2 kodelistePersister;

    public StoreTestStorePersisterStrategy2(HibernateStoreSession2 persister1, HashStorePersister2 persister2, KodelistePersister2 kodelistePersister) {
        this.persister1 = persister1;
        this.persister2 = persister2;
        this.kodelistePersister = kodelistePersister;
    }

    @Override
    public StorePersister2 getPersister(BubbleId2 bubbleId) {
        if (bubbleId instanceof KodeId2) {
            return kodelistePersister;
        } else if(bubbleId instanceof KodelisteId2) {
            return kodelistePersister;
        } else {
            return persister1;
        }
    }
}