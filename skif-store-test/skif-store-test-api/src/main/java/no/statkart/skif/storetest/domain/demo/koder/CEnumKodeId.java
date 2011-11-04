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
public class CEnumKodeId extends EnumKodeId<CEnumKode> implements StoreTestEnumKodeId<CEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>>(CEnumKodeId.class, new StoreTestEnumKodelisteLongId(3), "TestCEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static CEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static CEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static CEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected CEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static CEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(CEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static CEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(CEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
