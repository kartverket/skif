package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.SnapshotVersion;
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
