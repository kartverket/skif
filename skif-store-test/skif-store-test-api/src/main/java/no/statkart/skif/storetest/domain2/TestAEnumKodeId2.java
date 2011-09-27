package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeSupport2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestAEnumKodeId2 extends EnumKodeIdImpl2<TestAEnumKode2> implements TestEnumKodeId2<TestAEnumKode2> {
    private static EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>> kodeSupport = new EnumKodeSupport2<TestEnumKodeliste2, TestEnumKodelisteId2<TestEnumKodeliste2>>(TestAEnumKodeId2.class,new TestEnumKodelisteIdImpl2(1), "TestAEnumKodeliste");

    public static TestEnumKodelisteId2<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestAEnumKodeId2 IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestAEnumKodeId2 KodeAId = define(1, "A", "Kode A");
    public static TestAEnumKodeId2 KodeBId = define(2, "B", "Kode B");

    protected TestAEnumKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected EnumKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestAEnumKodeId2 define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.define(TestAEnumKodeId2.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestAEnumKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestAEnumKodeId2.class, idValue, ReplicaVersion2.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
