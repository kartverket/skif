package no.statkart.skif.storetest.service2.kodeliste2;

import com.google.inject.Inject;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.finder.KodelisteFinder2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;
import no.statkart.skif.store2.persistence.kodeliste.KodelistePersister2;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodelisteService2Impl implements KodelisteService2 {
    @Inject
    private KodelisteFinder2 kodelisteFinder;

    @Inject
    private KodelistePersister2 kodelistePersister;

    @Override
    public Collection<? extends KodelisteId2> getKodelisteIds() {
        return (Collection<? extends KodelisteId2>) kodelisteFinder.getKodelisteIds();
    }

    @Override
    public KodelisteTransfer2 getKodelister() {
        KodelisteTransfer2 kodelisteTransfer = kodelistePersister.getKodelisteTransfer();
        return new KodelisteTransfer2(kodelisteTransfer.getKodeIds(), kodelisteTransfer.getKodelisteIds(), kodelisteTransfer.getObjects());
    }
}
