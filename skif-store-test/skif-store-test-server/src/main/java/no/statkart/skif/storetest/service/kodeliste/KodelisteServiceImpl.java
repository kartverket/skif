package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.persistence.finder.BubbleKodelisteFinder;
import no.statkart.skif.store.persistence.kodeliste.BubbleKodelistePersister;
import no.statkart.skif.store.BubbleKodelisteTransfer;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteTransfer;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodelisteServiceImpl implements KodelisteService {
    @Inject
    private BubbleKodelisteFinder kodelisteFinder;

    @Inject
    private BubbleKodelistePersister kodelistePersister;

    @Override
    public Collection<? extends KodelisteId> getKodelisteIds() {
        return (Collection<? extends KodelisteId>) kodelisteFinder.getKodelisteIds();
    }

    @Override
    public KodelisteTransfer getKodelister() {
        BubbleKodelisteTransfer kodelisteTransfer = kodelistePersister.getKodelisteTransfer();
        return new KodelisteTransfer(kodelisteTransfer.getKodeIds(), kodelisteTransfer.getKodelisteIds(), kodelisteTransfer.getObjects());
    }
}
