package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class C1DbKodeId extends CDbKodeId<C1DbKode> {
    private static StoreTestDbKodeSupport<C1DbKodeId> kodeSupport = new StoreTestDbKodeSupport<C1DbKodeId>(C1DbKodeId.class, 10003L);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static C1DbKodeId C1AId = define(1);
    public static C1DbKodeId C1BId = define(2);

    public  C1DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static C1DbKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    /**
     * Typesikker sammenlikning av kodeId som ikke tar hensyn til {@code SnapshotVersion}
     */
    public boolean equalTo(C1DbKodeId id) {
        return this.equalsIgnoreSnapshotVersion(id);
    }

}
