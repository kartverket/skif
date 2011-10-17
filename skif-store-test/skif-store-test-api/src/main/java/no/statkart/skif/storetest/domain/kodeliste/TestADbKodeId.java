package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.store.kodelistesupport.DbKodelisteId;
import no.statkart.skif.storetest.domain.TestDbKodeId;
import no.statkart.skif.storetest.domain.TestDbKodeliste;
import no.statkart.skif.storetest.domain.TestDbKodelisteId;
import no.statkart.skif.storetest.domain.TestKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeId extends DbKodeIdImpl<TestADbKode> implements TestDbKodeId<TestADbKode> {
    private static DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>> kodeSupport = new DbKodeSupport<TestDbKodeliste, TestDbKodelisteId<TestDbKodeliste>>(TestADbKodeId.class,new TestDbKodelisteId(10001L, SnapshotVersion.CURRENT));

    public static TestKodelisteId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestADbKodeId A1Id = define(1);
    public static TestADbKodeId A2Id = define(2);

    protected TestADbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestADbKodeId define(long idValue) {
        return kodeSupport.define(TestADbKodeId.class, idValue);
    }

    public static TestADbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestADbKodeId.class, idValue, SnapshotVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}

