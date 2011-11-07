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
public class XStrDbKodeId extends DbKodeId<XStrDbKode> implements StoreTestDbKodeId<XStrDbKode> {
    private static DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>> kodeSupport = new DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>>(XStrDbKodeId.class,new StoreTestDbKodelisteLongId(10001L, SnapshotVersion.CURRENT));

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
//    public static XStrDbKodeId AId = define("A");
//    public static XStrDbKodeId BId = define("B");

    @Override
    public String getValue() {
        return (String) super.getValue();
    }

    protected XStrDbKodeId(String value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }


    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static XStrDbKodeId define(String idValue) {
        return kodeSupport.define(XStrDbKodeId.class, idValue);
    }

    public static XStrDbKodeId createInstance(String idValue) {
        return kodeSupport.createInstance(XStrDbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}

