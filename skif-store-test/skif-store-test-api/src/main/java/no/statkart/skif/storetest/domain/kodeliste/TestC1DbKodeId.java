package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC1DbKodeId extends TestCDbKodeId<TestC1DbKode> {
    private static DbKodeSupport kodeSupport = new DbKodeSupport(TestC1DbKodeId.class, 10003);

    public static KodelisteId KODELISTE_ID = kodeSupport.getKodelisteId();
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
