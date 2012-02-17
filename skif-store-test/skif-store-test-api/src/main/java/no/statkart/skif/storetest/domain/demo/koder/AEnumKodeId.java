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
    public static AEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static AEnumKodeId KodeAId = define(1, "A", "KodeA");
    public static AEnumKodeId KodeBId = define(2, "B", "KodeB");

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

    protected static AEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(idValue, kodeVerdi, beskrivelesesKey).getId();
    }

}
