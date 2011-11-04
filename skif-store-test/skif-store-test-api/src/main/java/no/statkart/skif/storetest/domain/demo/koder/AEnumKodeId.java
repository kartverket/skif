package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeId;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AEnumKodeId extends EnumKodeId<AEnumKode> implements StoreTestEnumKodeId<AEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>>(AEnumKodeId.class,new StoreTestEnumKodelisteLongId(1), "TestAEnumKodeliste");

    public static StoreTestEnumKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static AEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static AEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static AEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected AEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static AEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(AEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static AEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(AEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
