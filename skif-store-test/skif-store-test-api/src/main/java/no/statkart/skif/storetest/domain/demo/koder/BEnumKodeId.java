package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class BEnumKodeId extends StoreTestEnumKodeId<BEnumKode> {
    private static StoreTestEnumKodeSupport<BEnumKode, BEnumKodeId> kodeSupport = new StoreTestEnumKodeSupport<>(BEnumKodeId.class, 2, "no.statkart.skif.storetest.lokalisering.DemoKodeMsg");

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static BEnumKodeId IkkeOppgittId = define(0, "IkkeOppgitt", "-");
    public static BEnumKodeId KodeAId = define(1, "KodeA", "A");
    public static BEnumKodeId KodeBId = define(2, "KodeB", "B");
    public static BEnumKodeId KodeCId = define(1000000000L, "KodeC", "C");

    public BEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static BEnumKodeId define(long idValue, String kodeResourceKey, String kodeVerdi) {
        BEnumKode enumKode = kodeSupport.defineKode(idValue, kodeResourceKey);
        enumKode.setKodeverdi(kodeVerdi);
        return enumKode.getId();
    }
}
