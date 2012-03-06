package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ADbKodeId extends StoreTestDbKodeId<ADbKode> {
    private static StoreTestDbKodeSupport<ADbKodeId> kodeSupport = new StoreTestDbKodeSupport<ADbKodeId>(ADbKodeId.class, 10001L);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static ADbKodeId A1Id = define(1);
    public static ADbKodeId A2Id = define(2);

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ADbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static ADbKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    /**
     * Typesikker sammenlikning av kodeId som ikke tar hensyn til {@code SnapshotVersion}
     */
    public boolean equalTo(ADbKodeId id) {
        return this.equalsIgnoreSnapshotVersion(id);
    }

}

