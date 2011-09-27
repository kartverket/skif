package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeSupport2;
import no.statkart.skif.storetest.domain2.TestDbKodeId2;
import no.statkart.skif.storetest.domain2.TestDbKodeliste2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteId2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestBDbKodeId2 extends DbKodeIdImpl2<TestBDbKode2> implements TestDbKodeId2<TestBDbKode2> {
    private static DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>> kodeSupport = new DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>>(TestBDbKodeId2.class,new TestDbKodelisteIdImpl2(10002L, ReplicaVersion2.CURRENT));

    public static TestDbKodelisteId2<TestDbKodeliste2> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestBDbKodeId2 B1Id = define(1);
    public static TestBDbKodeId2 B2Id = define(2);

    protected TestBDbKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestBDbKodeId2 define(long idValue) {
        return kodeSupport.define(TestBDbKodeId2.class, idValue);
    }

    public static TestBDbKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestBDbKodeId2.class, idValue, ReplicaVersion2.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
