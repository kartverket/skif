package no.statkart.skif.storetest.domain2;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store2.kodelistesupport2.*;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodeSupportTest2 {
    /**
     * Denne klasse er spesial konstruert for å kunne test KodeSupport og brukes kun for dette.
     * KodeId klasser skal normalt ikke ha public constructor og må implementere {@link #getKodeSupport()}.
     * <p/>
     * I mottsettning til det som normalt skal gjelde for KodeId klasser så er det mulig å opprette flere instanser av
     * denne klassen med samme idvalue slik at det er mulig å teste at KodeSupport gjør jobben sin.
     */
    public static class TestEnumKodeId2 extends EnumKodeIdImpl2<EnumKodeImpl2> {
        public TestEnumKodeId2(long value, SnapshotVersion replicaVersion) {
            super(new Long(value), replicaVersion);
        }

        @Override
        protected EnumKodeSupportHelper2 getKodeSupport() {
            return null;
        }
    }

    public static class TestKodeSupport2 extends KodeSupportHelper2 {
        public TestKodeSupport2(Class<? extends KodeIdImpl2<?>> idClass, KodelisteIdImpl2 kodelisteId) {
            super(idClass, kodelisteId);
        }

        @Override
        protected <T extends Kode2> String getBeskrivelse(T kode, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
        @Override
        protected <T extends Kodeliste2> String getBeskrivelse(T kodeliste, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    public void testKodeListeid() {
        KodeSupportHelper2 kodeSupport = new TestKodeSupport2(null, new KodelisteIdImpl2(5));
        KodelisteId2 id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));
    }

    public void testCreateKodeId() {
        KodeSupportHelper2 kodeSupport = new TestKodeSupport2(null, new KodelisteIdImpl2(5));
        KodeIdImpl2<KodeImpl2> id = kodeSupport.getInstance(new Long(1), SnapshotVersion.CURRENT);
        assertNull(id);

        TestEnumKodeId2 id1 = new TestEnumKodeId2(1, SnapshotVersion.CURRENT);
        KodeIdImpl2<EnumKodeImpl2> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        assertSame(id1, kodeId1);

        TestEnumKodeId2 id1a = new TestEnumKodeId2(1, SnapshotVersion.CURRENT);
        KodeIdImpl2<EnumKodeImpl2> kodeId1a = kodeSupport.getOrCreateInstance(id1a);
        assertSame(id1, kodeId1a);
    }

    public void testNewKodeNotAllowed() {
        KodeSupportHelper2 kodeSupport = new TestKodeSupport2(null, new KodelisteIdImpl2(5));
        TestEnumKodeId2 id1 = new TestEnumKodeId2(1, SnapshotVersion.CURRENT);

        KodeIdImpl2<EnumKodeImpl2> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        kodeSupport.setNewKoderAllowed(false);

        // Test ok to get existing codes
        TestEnumKodeId2 id1a = new TestEnumKodeId2(1, SnapshotVersion.CURRENT);
        KodeIdImpl2<EnumKodeImpl2> kodeId1a = kodeSupport.getOrCreateInstance(id1a);

        // Test ok to sjekk for new code
        KodeIdImpl2<KodeImpl2> kodeId2 = kodeSupport.getInstance(new Long(2), SnapshotVersion.CURRENT);
        assertNull(kodeId2);

        // Not ok to create new codes
        TestEnumKodeId2 id2 = new TestEnumKodeId2(2, SnapshotVersion.CURRENT);
        try {
            KodeIdImpl2<EnumKodeImpl2> kodeId2a = kodeSupport.getOrCreateInstance(id2);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }

        // Ok to create old ReplicaVersions of existing codes
        TestEnumKodeId2 id1_old = new TestEnumKodeId2(1, SnapshotVersion.OLD);
        KodeIdImpl2<EnumKodeImpl2> kodeId1a_old = kodeSupport.getOrCreateInstance(id1_old);
        assertSame(id1_old, kodeId1a_old);


        // Not ok to create old ReplicaVersions of new codes
        TestEnumKodeId2 id2_old = new TestEnumKodeId2(2, SnapshotVersion.OLD);
        try {
            KodeIdImpl2<EnumKodeImpl2> kodeId2a_old = kodeSupport.getOrCreateInstance(id2_old);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }
    }
}


abstract class KodeSupportHelper2 extends KodeSupport2<Kodeliste2, KodelisteId2<Kodeliste2>> {
    public KodeSupportHelper2(Class<? extends KodeIdImpl2<?>> idClass, KodelisteIdImpl2 kodelisteId) {
        super(idClass, kodelisteId);
    }
}
