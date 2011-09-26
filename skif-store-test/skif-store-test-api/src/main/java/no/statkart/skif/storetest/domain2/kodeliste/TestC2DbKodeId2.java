package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeSupport2;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.TestCDbKode;
import no.statkart.skif.storetest.domain.kodeliste.TestCDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeSupport;
import no.statkart.skif.storetest.domain2.TestDbKodeliste2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteId2;
import no.statkart.skif.storetest.domain2.TestDbKodelisteIdImpl2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestC2DbKodeId2 extends TestCDbKodeId2<TestCDbKode2> {
    private static DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>> kodeSupport = new DbKodeSupport2<TestDbKodeliste2, TestDbKodelisteId2<TestDbKodeliste2>>(TestC2DbKodeId2.class,new TestDbKodelisteIdImpl2(10004L, ReplicaVersion2.CURRENT));


    public static TestDbKodelisteId2<TestDbKodeliste2> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static TestC2DbKodeId2 C2A1Id = define(10);
    public static TestC2DbKodeId2 C2BId = define(11);

    protected TestC2DbKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    protected DbKodeSupport2 getKodeSupport() {
        return kodeSupport;
    }

    protected static TestC2DbKodeId2 define(long idValue) {
        return kodeSupport.define(TestC2DbKodeId2.class, idValue);
    }

    public static TestC2DbKodeId2 createInstance(long idValue) {
        return kodeSupport.createInstance(TestC2DbKodeId2.class, idValue, ReplicaVersion2.CURRENT);
    }

    private Object readResolve() {
        return resolveInstance();
    }

}
