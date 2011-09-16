package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBEnumKodeId extends EnumKodeId<TestBEnumKode> {
    private static EnumKodeSupport kodeSupport = new EnumKodeSupport(TestBEnumKodeId.class, 2, "TestBEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestBEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static TestBEnumKodeId KodeBId = define(2, "B", "Kode B");
    public static TestBEnumKodeId KodeCId = define(1000000000L, "C", "Kode C");

    protected TestBEnumKodeId(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
//        return define(TestBEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey);
        return kodeSupport.define(TestBEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestBEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestBEnumKodeId.class, idValue, ReplicaVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
