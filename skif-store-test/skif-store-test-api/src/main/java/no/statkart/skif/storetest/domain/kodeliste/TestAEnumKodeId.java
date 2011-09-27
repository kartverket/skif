package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestAEnumKodeId extends EnumKodeId<TestAEnumKode> {
    private static EnumKodeSupport kodeSupport = new EnumKodeSupport(TestAEnumKodeId.class, 1, "TestAEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestAEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestAEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static TestAEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected TestAEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestAEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.define(TestAEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestAEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestAEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
