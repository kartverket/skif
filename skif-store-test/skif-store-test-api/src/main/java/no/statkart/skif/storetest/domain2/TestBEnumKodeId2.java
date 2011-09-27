package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeSupport2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBEnumKodeId2 extends EnumKodeIdImpl2<TestBEnumKode2> implements TestEnumKodeId2<TestBEnumKode2>{
    private static EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>> kodeSupport = new EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>>(TestBEnumKodeId2.class, new TestEnumKodelisteIdImpl2(2), "TestBEnumKodeliste");

    public static KodelisteId2<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBEnumKodeId2 IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestBEnumKodeId2 KodeAId = define(1, "A", "Kode A");
    public static TestBEnumKodeId2 KodeBId = define(2, "B", "Kode B");
    public static TestBEnumKodeId2 KodeCId = define(1000000000L, "C", "Kode C");

    protected TestBEnumKodeId2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected EnumKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBEnumKodeId2 define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.define(TestBEnumKodeId2.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestBEnumKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestBEnumKodeId2.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
