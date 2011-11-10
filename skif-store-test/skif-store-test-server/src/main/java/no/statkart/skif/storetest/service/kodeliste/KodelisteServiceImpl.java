package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.finder.KodelisteFinder;
import no.statkart.skif.store.persistence.kodeliste.KodelistePersister;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodelisteServiceImpl implements KodelisteService {
    @Inject
    private KodelisteFinder kodelisteFinder;

    @Inject
    private KodelistePersister kodelistePersister;

    @Override
    public Collection<? extends KodelisteId> getKodelisteIds() {
        return (Collection<? extends KodelisteId>) kodelisteFinder.getKodelisteIds();
    }

    @Override
    public KodelisteTransfer getKodelister() {
        KodelisteTransfer kodelisteTransfer = kodelistePersister.getKodelisteTransfer();
        return new KodelisteTransfer(kodelisteTransfer.getKodeIds(), kodelisteTransfer.getKodelisteIds(), kodelisteTransfer.getObjects());
    }

    @Override
    public String getKodelisterTest() {
        return "Hello Impl";
    }
}
