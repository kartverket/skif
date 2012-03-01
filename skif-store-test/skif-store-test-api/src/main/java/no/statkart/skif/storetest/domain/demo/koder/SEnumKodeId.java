package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId;

/**
 * En enum kode som bruke en kodeliste med string idValue
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SEnumKodeId extends StoreTestEnumKodeId<SEnumKode> {
    private static EnumKodeSupport<SEnumKode, SEnumKodeId, StoreTestKodelisteString, StoreTestKodelisteStringId<StoreTestKodelisteString>> kodeSupport = new EnumKodeSupport<SEnumKode, SEnumKodeId, StoreTestKodelisteString, StoreTestKodelisteStringId<StoreTestKodelisteString>>(SEnumKodeId.class, new StoreTestKodelisteStringId("TestSEnumKodeliste"), "DemoMsg");

    public static StoreTestKodelisteStringId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static SEnumKodeId IkkeOppgittId = define(0, "IkkeOppgitt", "-");
    public static SEnumKodeId KodeAId = define(1, "KodeA", "A");
    public static SEnumKodeId KodeBId = define(2, "KodeB", "B");

    public SEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public StoreTestKodelisteStringId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static SEnumKodeId define(long idValue, String kodeResourceKey, String kodeVerdi) {
        SEnumKode enumKode = kodeSupport.defineKode(idValue, kodeResourceKey);
        enumKode.setKodeverdi(kodeVerdi);
        return enumKode.getId();
    }

}
