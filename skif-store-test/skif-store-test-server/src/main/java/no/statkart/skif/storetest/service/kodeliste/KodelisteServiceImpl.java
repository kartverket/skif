package no.statkart.skif.storetest.service.kodeliste;

import com.google.inject.Inject;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.persistence.finder.KodelisteFinder;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteServiceImpl extends no.statkart.skif.store.kodeliste.KodelisteServiceImpl implements KodelisteService {
    @Override
    public <I extends KodelisteId<?>> KodelisteTransfer<I> getKodeliste(String kodeIdClassName, SnapshotVersion snapshotVersion) {
        return super.getKodeliste(kodeIdClassName, snapshotVersion);
    }

    @Override
    public <I extends KodelisteId<?>> KodelisteTransfer<I> getKodelister(SnapshotVersion snapshotVersion) {
        return super.getKodelister(snapshotVersion);
    }
}
