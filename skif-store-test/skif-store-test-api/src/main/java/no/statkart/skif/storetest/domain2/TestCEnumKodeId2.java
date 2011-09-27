package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeSupport2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestCEnumKodeId2 extends EnumKodeIdImpl2<TestCEnumKode2> implements TestEnumKodeId2<TestCEnumKode2> {
    private static EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>> kodeSupport = new EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>>(TestCEnumKodeId2.class, new TestEnumKodelisteIdImpl2(3), "TestCEnumKodeliste");

    public static KodelisteId2<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestCEnumKodeId2 IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestCEnumKodeId2 KodeAId = define(1, "A", "Kode A");
    public static TestCEnumKodeId2 KodeBId = define(2, "B", "Kode B");

    protected TestCEnumKodeId2(Long value, SnapshotVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected EnumKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestCEnumKodeId2 define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.define(TestCEnumKodeId2.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestCEnumKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestCEnumKodeId2.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
