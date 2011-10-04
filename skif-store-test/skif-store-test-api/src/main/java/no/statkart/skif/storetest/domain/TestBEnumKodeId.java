package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.EnumKodeSupport;
import no.statkart.skif.store.kodelistesupport.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBEnumKodeId extends EnumKodeIdImpl<TestBEnumKode> implements TestEnumKodeId<TestBEnumKode> {
    private static EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>> kodeSupport = new EnumKodeSupport<TestEnumKodeliste, TestEnumKodelisteId<TestEnumKodeliste>>(TestBEnumKodeId.class, new TestEnumKodelisteId(2), "TestBEnumKodeliste");

    public static KodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBEnumKodeId IkkeOppgittId = define(0, "-", "IkkeOppgitt");
    public static TestBEnumKodeId KodeAId = define(1, "A", "Kode A");
    public static TestBEnumKodeId KodeBId = define(2, "B", "Kode B");
    public static TestBEnumKodeId KodeCId = define(1000000000L, "C", "Kode C");

    protected TestBEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected EnumKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBEnumKodeId define(long idValue, String kodeVerdi, String beskrivelesesKey) {
        return kodeSupport.define(TestBEnumKodeId.class, idValue, kodeVerdi, beskrivelesesKey);
    }

    public static TestBEnumKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestBEnumKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
