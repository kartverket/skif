package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.store.persistence.finder.KodelisteFinder;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteServiceImpl implements KodelisteService {
    @Inject
    private KodelisteFinder kodelisteFinder;

    @Override
    public Collection<? extends KodelisteId> getKodelisteIds() {
        return (Collection<? extends KodelisteId>) kodelisteFinder.getKodelisteIds();
    }

    @Override
    public KodelisteTransfer getKodelister() {
        // TODO Fix
        KodelisteTransfer kodelisteTransfer = null; //kodelistePersister.getKodelisteTransfer();
        return new KodelisteTransfer(kodelisteTransfer.getKodeIds(), kodelisteTransfer.getKodelisteIds(), kodelisteTransfer.getObjects());
    }

    @Override
    public String getKodelisterTest() {
        return "Hello Impl";
    }
}
