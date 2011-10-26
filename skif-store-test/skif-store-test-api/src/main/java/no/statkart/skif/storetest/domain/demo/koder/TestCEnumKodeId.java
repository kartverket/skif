package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.store.kodelistesupport.KodelisteId;
import no.statkart.skif.storetest.domain.kode.TestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestEnumKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.TestEnumKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestCEnumKodeId extends EnumKodeIdImpl<TestCEnumKode> implements TestEnumKodeId<TestCEnumKode> {
    private static EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>> kodeSupport = new EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>>(TestCEnumKodeId.class, new TestEnumKodelisteId(3), "TestCEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestCEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestCEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static TestCEnumKodeId KodeBId = define(2, "B", "Kode B");

    protected TestCEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestCEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.defineKode(TestCEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey).getId();
    }

    public static TestCEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestCEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
