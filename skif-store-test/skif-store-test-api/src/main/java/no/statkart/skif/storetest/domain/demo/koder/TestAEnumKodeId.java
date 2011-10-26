package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kode.TestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestEnumKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.TestEnumKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.TestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class TestAEnumKodeId extends EnumKodeIdImpl<TestAEnumKode> implements TestEnumKodeId<TestAEnumKode> {
    private static EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>> kodeSupport = new EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>>(TestAEnumKodeId.class,new TestEnumKodelisteId(1), "TestAEnumKodeliste");

    public static TestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
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
        return kodeSupport.defineKode(TestAEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static TestAEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestAEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
