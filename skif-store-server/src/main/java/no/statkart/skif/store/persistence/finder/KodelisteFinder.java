package no.statkart.skif.store.persistence.finder;


import com.google.inject.Inject;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteFinder {
    private final Store store;

    @Inject
    public KodelisteFinder( Store store) {
        this.store = store;
    }

    public Collection<? extends KodelisteId<?>> getKodelisteIds() {
        return null; //kodelistePersister.getKodelisterIds();
    }

    public Collection<? extends KodeId<?>> getKodeIds() {
        return null; //kodelistePersister.getKoderIds();
    }

    public Collection<? extends BubbleObject> getAllKodelisterAndKoder() {
        return null; //store.register(kodelistePersister.getAllKodelisterAndKoder(), new ArrayList<BubbleObject>());
    }
}

