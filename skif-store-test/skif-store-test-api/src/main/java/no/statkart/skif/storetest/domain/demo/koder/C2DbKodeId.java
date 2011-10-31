package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class C2DbKodeId extends CDbKodeId<CDbKode> {
    private static DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>> kodeSupport = new DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>>(C2DbKodeId.class,new StoreTestDbKodelisteId(10004L, SnapshotVersion.CURRENT));


    public static StoreTestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static C2DbKodeId C2A1Id = define(10);
    public static C2DbKodeId C2BId = define(11);

    protected C2DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static C2DbKodeId define(long idValue) {
        return kodeSupport.define(C2DbKodeId.class, idValue);
    }

    public static C2DbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(C2DbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
