package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.store.kodelistesupport.DbKodeIdImpl;
import no.statkart.skif.storetest.domain.TestDbKodeId;
import no.statkart.skif.storetest.domain.TestDbKodeliste;
import no.statkart.skif.storetest.domain.TestDbKodelisteId;
import no.statkart.skif.storetest.domain.TestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKodeId extends DbKodeIdImpl<TestBDbKode> implements TestDbKodeId<TestBDbKode> {
    private static DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>> kodeSupport = new DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>>(TestBDbKodeId.class,new TestDbKodelisteId(10002L, SnapshotVersion.CURRENT));

    public static TestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBDbKodeId B1Id = define(1);
    public static TestBDbKodeId B2Id = define(2);

    protected TestBDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBDbKodeId define(long idValue) {
        return kodeSupport.define(TestBDbKodeId.class, idValue);
    }

    public static TestBDbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestBDbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
