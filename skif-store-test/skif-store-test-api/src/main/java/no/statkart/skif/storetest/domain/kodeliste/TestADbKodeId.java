package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeId extends DbKodeId<TestBDbKode> {
    private static DbKodeSupport kodeSupport = new DbKodeSupport(TestADbKodeId.class, 10001);

    public static DbKodelisteId KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestADbKodeId A1Id = define(1);
    public static TestADbKodeId A2Id = define(2);

    protected TestADbKodeId(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport getKodeSupport() {
        return kodeSupport;
    }

    protected static TestADbKodeId define(long idValue) {
        return kodeSupport.define(TestADbKodeId.class, idValue);
    }

    public static TestADbKodeId createInstance(long idValue) {
        return kodeSupport.createInstance(TestADbKodeId.class, idValue, ReplicaVersion.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
