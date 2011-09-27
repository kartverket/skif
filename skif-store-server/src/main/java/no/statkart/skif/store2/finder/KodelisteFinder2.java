package no.statkart.skif.store2.finder;

import com.google.inject.Inject;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.Store2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;
import no.statkart.skif.store2.persistence.kodeliste.KodelistePersister2;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteFinder2 {
    private final KodelistePersister2 kodelistePersister;
    private final Store2 store;

    @Inject
    public KodelisteFinder2(KodelistePersister2 kodelistePersister, Store2 store) {
        this.kodelistePersister = kodelistePersister;
        this.store = store;
    }

    public Collection<? extends KodelisteId2<?>> getKodelisteIds() {
        return kodelistePersister.getKodelisteIds();
    }

    public Collection<? extends KodeId2<?>> getKodeIds() {
        return kodelistePersister.getKodeIds();
    }

    public Collection<? extends BubbleObject2> getAllKodelisterAndKoder() {
        return store.register(kodelistePersister.getAllKodelisterAndKoder(), new ArrayList<BubbleObject2>());
    }
}
