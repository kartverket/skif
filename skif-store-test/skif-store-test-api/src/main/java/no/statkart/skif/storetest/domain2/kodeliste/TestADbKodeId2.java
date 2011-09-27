package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.*;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;
import no.statkart.skif.storetest.domain2.TestDbKodeId2;
import no.statkart.skif.storetest.domain2.TestDbKodeliste2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteId2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestADbKodeId2 extends DbKodeIdImpl2<TestADbKode2> implements TestDbKodeId2<TestADbKode2> {
    private static DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>> kodeSupport = new DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>>(TestADbKodeId2.class,new TestDbKodelisteIdImpl2(10001L, ReplicaVersion2.CURRENT));

    public static DbKodelisteId2 KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestADbKodeId2 A1Id = define(1);
    public static TestADbKodeId2 A2Id = define(2);

    protected TestADbKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestADbKodeId2 define(long idValue) {
        return kodeSupport.define(TestADbKodeId2.class, idValue);
    }

    public static TestADbKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestADbKodeId2.class, idValue, ReplicaVersion2.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
