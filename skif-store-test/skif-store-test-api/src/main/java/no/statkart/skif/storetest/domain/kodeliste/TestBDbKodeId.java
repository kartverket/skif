package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKodeId extends DbKodeId<TestBDbKode> {
    private static DbKodeSupport kodeSupport = new DbKodeSupport(TestBDbKodeId.class, 10002);

    public static DbKodelisteId KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBDbKodeId B1Id = define(1);
    public static TestBDbKodeId B2Id = define(2);

    protected TestBDbKodeId(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBDbKodeId define(long idValue) {
        return kodeSupport.define(TestBDbKodeId.class, idValue);
    }

    public static TestBDbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestBDbKodeId.class, idValue, ReplicaVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
