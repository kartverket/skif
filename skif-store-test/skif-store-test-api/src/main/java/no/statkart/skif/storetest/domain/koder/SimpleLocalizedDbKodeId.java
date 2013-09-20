package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * Id for {@link SimpleLocalizedDbKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SimpleLocalizedDbKodeId extends HistoriskDbKodeId<SimpleLocalizedDbKode> {
    private static final long serialVersionUID = 1L;

    private static final StoreTestDbKodeSupport<SimpleLocalizedDbKodeId> kodeSupport = new StoreTestDbKodeSupport<SimpleLocalizedDbKodeId>(SimpleLocalizedDbKodeId.class, 10011L);

    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();

    public SimpleLocalizedDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID;
    }
}
