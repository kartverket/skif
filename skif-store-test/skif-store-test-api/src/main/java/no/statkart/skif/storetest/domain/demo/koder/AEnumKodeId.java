package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class AEnumKodeId extends StoreTestEnumKodeId<AEnumKode> {
    private static StoreTestEnumKodeSupport<AEnumKode, AEnumKodeId> kodeSupport = new StoreTestEnumKodeSupport<AEnumKode, AEnumKodeId>(AEnumKodeId.class, 1, "demoKodeMsg");

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static AEnumKodeId IkkeOppgittId = define(0, "IkkeOppgitt", "-");
    public static AEnumKodeId KodeAId = define(1, "KodeA", "A");
    public static AEnumKodeId KodeBId = define(2, "KodeB", "B");

    public Long getValue() {
        return (Long) super.getValue();
    }

    public AEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static AEnumKodeId define(long idValue, String kodeResourceKey, String kodeVerdi) {
        AEnumKode enumKode = kodeSupport.defineKode(idValue, kodeResourceKey);
        enumKode.setKodeverdi(kodeVerdi);
        return enumKode.getId();
    }

}
