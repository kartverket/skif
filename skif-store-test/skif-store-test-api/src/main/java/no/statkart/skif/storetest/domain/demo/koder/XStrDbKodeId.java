package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class XStrDbKodeId extends StoreTestDbKodeId<XStrDbKode> {
    private static StoreTestDbKodeSupport<XStrDbKodeId> kodeSupport = new StoreTestDbKodeSupport<XStrDbKodeId>(XStrDbKodeId.class, 10005);

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static XStrDbKodeId AId = define("A");
    public static XStrDbKodeId BId = define("B");

    @Override
    public String getValue() {
        return (String) super.getValue();
    }

    public XStrDbKodeId(String value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    protected static XStrDbKodeId define(String idValue) {
        return kodeSupport.defineId(idValue);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

}

