package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeId;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class ADbKodeId extends DbKodeId<ADbKode> implements StoreTestDbKodeId<ADbKode> {
    private static DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>> kodeSupport = new DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>>(ADbKodeId.class,new StoreTestDbKodelisteLongId(10001L, SnapshotVersion.CURRENT));

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static ADbKodeId A1Id = define(1);
    public static ADbKodeId A2Id = define(2);

    protected ADbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static ADbKodeId define(long idValue) {
        return kodeSupport.define(ADbKodeId.class, idValue);
    }

    public static ADbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(ADbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}

