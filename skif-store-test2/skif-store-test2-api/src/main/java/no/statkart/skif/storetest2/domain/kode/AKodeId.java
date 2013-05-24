package no.statkart.skif.storetest2.domain.kode;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2DbKodeId;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2DbKodeSupport;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2KodelisteLongId;

/**
 * Id for {@link AKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.1
 */
public class AKodeId extends StoreTest2DbKodeId<AKode> {
    private static StoreTest2DbKodeSupport<AKodeId> kodeSupport = new StoreTest2DbKodeSupport<AKodeId>(AKodeId.class, 10001L);

    public static StoreTest2KodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();

    public AKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        return KODELISTE_ID;
    }
}
