package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeImplId;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.store.kodelistesupport.KodelisteImplId;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BEnumKodeId extends EnumKodeImplId<BEnumKode> implements StoreTestEnumKodeId<BEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>>(BEnumKodeId.class, new StoreTestEnumKodelisteLongId(2), "TestBEnumKodeliste");

    public static KodelisteImplId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static BEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static BEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static BEnumKodeId KodeBId = define(2, "B", "Kode B");
    public static BEnumKodeId KodeCId = define(1000000000L, "C", "Kode C");

    protected BEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static BEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(BEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static BEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(BEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
