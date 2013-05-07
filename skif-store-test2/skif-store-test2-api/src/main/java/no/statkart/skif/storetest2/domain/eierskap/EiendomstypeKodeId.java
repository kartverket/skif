package no.statkart.skif.storetest2.domain.eierskap;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2EnumKodeId;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2EnumKodeSupport;
import no.statkart.skif.storetest2.domain.kodeliste.StoreTest2KodelisteLongId;

/**
 * Id for {@link EiendomstypeKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class EiendomstypeKodeId extends StoreTest2EnumKodeId<EiendomstypeKode> {
    private static StoreTest2EnumKodeSupport<EiendomstypeKode, EiendomstypeKodeId> kodeSupport = new StoreTest2EnumKodeSupport<EiendomstypeKode, EiendomstypeKodeId>(EiendomstypeKodeId.class, 1, "no.statkart.skif.storetest2.lokalisering.EiendomstypeKodeMsg");

    public static StoreTest2KodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();

    public static EiendomstypeKodeId FAST = define(1, "Fast");
    public static EiendomstypeKodeId FLYTENDE = define(2, "Flytende");

    @SuppressWarnings("UnusedDeclaration")
    public EiendomstypeKodeId(Long value) {
        super(value);
    }

    @SuppressWarnings("UnusedDeclaration")
    public EiendomstypeKodeId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public StoreTest2KodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID;
    }

    protected static EiendomstypeKodeId define(long idValue, String kodeResourceKey) {
        EiendomstypeKode enumKode = kodeSupport.defineKode(idValue, kodeResourceKey);
        return enumKode.getId();
    }
}
