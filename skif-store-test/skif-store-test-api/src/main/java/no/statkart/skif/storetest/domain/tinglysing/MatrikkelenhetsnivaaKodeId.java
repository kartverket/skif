package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class MatrikkelenhetsnivaaKodeId extends StoreTestEnumKodeId<MatrikkelenhetsnivaaKode> {
    private static StoreTestEnumKodeSupport<MatrikkelenhetsnivaaKode, MatrikkelenhetsnivaaKodeId> kodeSupport = new StoreTestEnumKodeSupport<MatrikkelenhetsnivaaKode, MatrikkelenhetsnivaaKodeId>(MatrikkelenhetsnivaaKodeId.class, 1, "maalform");

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static MatrikkelenhetsnivaaKodeId Grunn = define(0, "nb", "G", "Grunn");
    public static MatrikkelenhetsnivaaKodeId Feste = define(1, "nb", "F", "Feste");
    public static MatrikkelenhetsnivaaKodeId Framfeste1 = define(2, "nb", "F1", "Framfeste");
    public static MatrikkelenhetsnivaaKodeId Framfeste2 = define(3, "nb", "F2", "Framfeste 2");
    public static MatrikkelenhetsnivaaKodeId Framfeste3 = define(4, "nb", "F3", "Framfeste 3");
    public static MatrikkelenhetsnivaaKodeId Framfeste4 = define(5, "nb", "F4", "Framfeste 4");
    public static MatrikkelenhetsnivaaKodeId Framfeste5 = define(6, "nb", "F5", "Framfeste 5");
    public static MatrikkelenhetsnivaaKodeId Framfeste6 = define(7, "nb", "F6", "Framfeste 6");
    public static MatrikkelenhetsnivaaKodeId Framfeste7 = define(8, "nb", "F7", "Framfeste 7");
    public static MatrikkelenhetsnivaaKodeId Framfeste8 = define(9, "nb", "F8", "Framfeste 8");
    public static MatrikkelenhetsnivaaKodeId Framfeste9 = define(10, "nb", "F9", "Framfeste 9");

    public Long getValue() {
        return (Long) super.getValue();
    }

    public MatrikkelenhetsnivaaKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static MatrikkelenhetsnivaaKodeId define(long idValue, String kodeResourceKey, String ident, String kodeVerdi) {
        MatrikkelenhetsnivaaKode matrikkelenhetsnivaa = kodeSupport.defineKode(idValue, kodeResourceKey);
        matrikkelenhetsnivaa.setIdent(ident);
        matrikkelenhetsnivaa.setKodeverdi(kodeVerdi);
        return matrikkelenhetsnivaa.getId();
    }

}
