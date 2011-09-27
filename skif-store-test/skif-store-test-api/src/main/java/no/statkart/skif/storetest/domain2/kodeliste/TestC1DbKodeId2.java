package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeSupport2;
import no.statkart.skif.storetest.domain2.TestDbKodeliste2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteId2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC1DbKodeId2 extends TestCDbKodeId2<TestC1DbKode2> {
    private static DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>> kodeSupport = new DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>>(TestC1DbKodeId2.class,new TestDbKodelisteIdImpl2(10003L, ReplicaVersion2.CURRENT));

    public static TestDbKodelisteId2<TestDbKodeliste2> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestC1DbKodeId2 C1AId = define(1);
    public static TestC1DbKodeId2 C1BId = define(2);

    protected TestC1DbKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestC1DbKodeId2 define(long idValue) {
        return kodeSupport.define(TestC1DbKodeId2.class, idValue);
    }

    public static TestC1DbKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestC1DbKodeId2.class, idValue, ReplicaVersion2.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
