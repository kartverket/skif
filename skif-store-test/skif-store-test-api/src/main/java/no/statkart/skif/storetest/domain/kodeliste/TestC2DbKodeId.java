package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.storetest.domain.TestDbKodeliste;
import no.statkart.skif.storetest.domain.TestDbKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC2DbKodeId extends TestCDbKodeId<TestCDbKode> {
    private static DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>> kodeSupport = new DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>>(TestC2DbKodeId.class,new TestDbKodelisteId(10004L, SnapshotVersion.CURRENT));


    public static TestDbKodelisteId<TestDbKodeliste> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestC2DbKodeId C2A1Id = define(10);
    public static TestC2DbKodeId C2BId = define(11);

    protected TestC2DbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestC2DbKodeId define(long idValue) {
        return kodeSupport.define(TestC2DbKodeId.class, idValue);
    }

    public static TestC2DbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestC2DbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
