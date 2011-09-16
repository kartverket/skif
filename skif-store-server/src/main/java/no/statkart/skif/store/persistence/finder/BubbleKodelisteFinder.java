package no.statkart.skif.store.persistence.finder;

import com.google.inject.Inject;
import no.statkart.skif.store.persistence.kodeliste.BubbleKodelistePersister;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store.kodelistesupport.BubbleKodelisteId;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BubbleKodelisteFinder {
    private final BubbleKodelistePersister kodelistePersister;
    private final Store store;

    @Inject
    public BubbleKodelisteFinder(BubbleKodelistePersister kodelistePersister, Store store) {
        this.kodelistePersister = kodelistePersister;
        this.store = store;
    }

    public Collection<? extends BubbleKodelisteId<?>> getKodelisteIds() {
        return kodelistePersister.getKodelisteIds();
    }

    public Collection<? extends BubbleKodeId<?>> getKodeIds() {
        return kodelistePersister.getKodeIds();
    }

    public Collection<? extends BubbleObject> getAllKodelisterAndKoder() {
        return store.register(kodelistePersister.getAllKodelisterAndKoder(), new ArrayList<BubbleObject>());
    }
}
