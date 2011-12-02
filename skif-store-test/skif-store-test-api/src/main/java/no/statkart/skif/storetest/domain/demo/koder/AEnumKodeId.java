package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodeId;
import no.statkart.skif.store.kodeliste.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kode.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class AEnumKodeId extends EnumKodeId<AEnumKode> implements StoreTestEnumKodeId<AEnumKode> {
    private static EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>> kodeSupport = new EnumKodeSupport<StoreTestEnumKodelisteLong, StoreTestEnumKodelisteLongId<StoreTestEnumKodelisteLong>>(AEnumKodeId.class,new StoreTestEnumKodelisteLongId(1), "TestAEnumKodeliste.navn");

    public static String kodeMsgNavn = "demoKodeMsg";
    public static StoreTestEnumKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static AEnumKodeId IkkeOppgittId = define(0, "-", "Kode.IkkeOppgitt");
    public static AEnumKodeId KodeAId = define(1, "A", "AEnumKode.A");
    public static AEnumKodeId KodeBId = define(2, "B", "AEnumKode.B");

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

    private Object readResolve() {
        return resolveInstance();
    }

}
