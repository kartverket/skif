package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.store.kodelistesupport.DbKodeIdImpl;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BDbKodeId extends DbKodeIdImpl<BDbKode> implements StoreTestDbKodeId<BDbKode> {
    private static DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>> kodeSupport = new DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>>(BDbKodeId.class,new StoreTestDbKodelisteId(10002L, SnapshotVersion.CURRENT));

    public static StoreTestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static BDbKodeId B1Id = define(1);
    public static BDbKodeId B2Id = define(2);

    protected BDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static BDbKodeId define(long idValue) {
        return kodeSupport.define(BDbKodeId.class, idValue);
    }

    public static BDbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(BDbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
