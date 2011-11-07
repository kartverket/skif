package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodeId;
import no.statkart.skif.store.kodeliste.EnumKodeSupport;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class SEnumKodeId extends EnumKodeId<SEnumKode> implements StoreTestEnumKodeId<SEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>>(SEnumKodeId.class, new StoreTestEnumKodelisteLongId(3), "TestCEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static SEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static SEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static SEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected SEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static SEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(SEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
