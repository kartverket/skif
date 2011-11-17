package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class C1DbKodeId extends CDbKodeId<C1DbKode> {
    private static DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>> kodeSupport = new DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>>(C1DbKodeId.class,new StoreTestDbKodelisteLongId(10003L, SnapshotVersion.CURRENT));

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static C1DbKodeId C1AId = define(1);
    public static C1DbKodeId C1BId = define(2);

    public C1DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static C1DbKodeId define(long idValue) {
        return kodeSupport.define(C1DbKodeId.class, idValue);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
