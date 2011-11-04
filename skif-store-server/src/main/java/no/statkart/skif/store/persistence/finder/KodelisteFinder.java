package no.statkart.skif.store.persistence.finder;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteFinder {
    private final KodelistePersister kodelistePersister;
    private final Store store;

    @Inject
    public KodelisteFinder(KodelistePersister kodelistePersister, Store store) {
        this.kodelistePersister = kodelistePersister;
        this.store = store;
    }

    public Collection<? extends KodelisteId<?>> getKodelisteIds() {
        return kodelistePersister.getKodelisteIds();
    }

    public Collection<? extends KodeId<?>> getKodeIds() {
        return kodelistePersister.getKodeIds();
    }

    public Collection<? extends BubbleObject> getAllKodelisterAndKoder() {
        return store.register(kodelistePersister.getAllKodelisterAndKoder(), new ArrayList<BubbleObject>());
    }
}

