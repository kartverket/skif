package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class C2DbKodeId extends CDbKodeId<CDbKode> {
    private static StoreTestDbKodeSupport<C2DbKodeId> kodeSupport = new StoreTestDbKodeSupport<C2DbKodeId>(C2DbKodeId.class, 10004L);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static C2DbKodeId C2A1Id = define(10);
    public static C2DbKodeId C2BId = define(11);

    public  C2DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static C2DbKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }
    /**
     * Typesikker sammenlikning av kodeId som ikke tar hensyn til {@code SnapshotVersion}
     */
    public boolean equalTo(C2DbKodeId id) {
        return this.equalsIgnoreSnapshotVersion(id);
    }

}
