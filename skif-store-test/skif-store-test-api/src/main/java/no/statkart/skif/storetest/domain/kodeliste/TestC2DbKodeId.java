package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC2DbKodeId extends TestCDbKodeId<TestCDbKode> {
    private static DbKodeSupport kodeSupport = new DbKodeSupport(TestC2DbKodeId.class, 10004);

    public static KodelisteId KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestC2DbKodeId C2A1Id = define(10);
    public static TestC2DbKodeId C2BId = define(11);

    protected TestC2DbKodeId(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestC2DbKodeId define(long idValue) {
        return kodeSupport.define(TestC2DbKodeId.class, idValue);
    }

    public static TestC2DbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestC2DbKodeId.class, idValue, ReplicaVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
