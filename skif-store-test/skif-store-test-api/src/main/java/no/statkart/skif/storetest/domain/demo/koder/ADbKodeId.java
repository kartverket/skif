package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeSupport;
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

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    public ADbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }


    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static ADbKodeId define(long idValue) {
        return kodeSupport.define(ADbKodeId.class, idValue);
    }
}

