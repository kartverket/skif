package no.statkart.skif.storetest.service.kodeliste;

import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@SuppressWarnings("unused")
public class KodelisteServiceImpl extends no.statkart.skif.store.kodeliste.KodelisteServiceImpl implements KodelisteService {

    @Override
    public KodelisteTransfer<? extends KodelisteId<?>> getKodelister(SnapshotVersion snapshotVersion) {
        return super.getKodelister(snapshotVersion);
    }
}
