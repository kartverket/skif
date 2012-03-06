package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class BDbKodeId extends StoreTestDbKodeId<BDbKode> {
    private static StoreTestDbKodeSupport<BDbKodeId> kodeSupport = new StoreTestDbKodeSupport<BDbKodeId>(BDbKodeId.class, 10002L);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static BDbKodeId B1Id = define(1);
    public static BDbKodeId B2Id = define(2);

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public BDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static BDbKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    /**
     * Typesikker sammenlikning av kodeId som ikke tar hensyn til {@code SnapshotVersion}
     */
    public boolean equalTo(BDbKodeId id) {
        return this.equalsIgnoreSnapshotVersion(id);
    }

}
