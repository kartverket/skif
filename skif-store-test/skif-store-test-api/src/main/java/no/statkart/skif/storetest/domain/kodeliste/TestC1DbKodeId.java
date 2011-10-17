package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.storetest.domain.TestDbKodeliste;
import no.statkart.skif.storetest.domain.TestDbKodelisteId;
import no.statkart.skif.storetest.domain.TestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC1DbKodeId extends TestCDbKodeId<TestC1DbKode> {
    private static DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>> kodeSupport = new DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>>(TestC1DbKodeId.class,new TestDbKodelisteId(10003L, SnapshotVersion.CURRENT));

    public static TestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestC1DbKodeId C1AId = define(1);
    public static TestC1DbKodeId C1BId = define(2);

    protected TestC1DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestC1DbKodeId define(long idValue) {
        return kodeSupport.define(TestC1DbKodeId.class, idValue);
    }

    public static TestC1DbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestC1DbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
