package no.statkart.skif.storetest.domain;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.*;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class KodeSupportTest {
    /**
     * Denne klasse er spesial konstruert for å kunne test KodeSupport og brukes kun for dette.
     * KodeId klasser skal normalt ikke ha public constructor og må implementere {@link #getKodeSupport()}.
     * <p/>
     * I mottsettning til det som normalt skal gjelde for KodeId klasser så er det mulig å opprette flere instanser av
     * denne klassen med samme idvalue slik at det er mulig å teste at KodeSupport gjør jobben sin.
     */
    public static class TestEnumKodeId extends EnumKodeImplId<EnumKodeImpl> {
        public TestEnumKodeId(long value, SnapshotVersion snapshotVersion) {
            super(new Long(value), snapshotVersion);
        }

        @Override
        protected EnumKodeSupportHelper getKodeSupport() {
            return null;
        }
    }

    /**
     * Hjelper test klasse for å kunne lage en instanse av KodelisteId som bruker en long idValue
     * @param <T>
     */
    public static class TestKodelisteImplId<T extends Kodeliste> extends KodelisteId<T> {
        @Override
        public Long getValue() {
            return (Long)super.getValue();
        }

        public TestKodelisteImplId(long value) {
            super(value);
        }
    }

    public static class TestKodeSupport extends KodeSupportHelper {
        public TestKodeSupport(Class<? extends KodeImplId<?>> idClass, KodelisteId kodelisteId) {
            super(idClass, kodelisteId);
        }

        @Override
        protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public void testKodeListeid() {
        KodeSupportHelper kodeSupport = new TestKodeSupport(null, new TestKodelisteImplId(5));
        KodelisteId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));
    }

    public void testCreateKodeId() {
        KodeSupportHelper kodeSupport = new TestKodeSupport(null, new TestKodelisteImplId(5));
        KodeImplId<KodeImpl> id = kodeSupport.getInstance(new Long(1), SnapshotVersion.CURRENT);
        assertNull(id);

        TestEnumKodeId id1 = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeImplId<EnumKodeImpl> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        assertSame(id1, kodeId1);

        TestEnumKodeId id1a = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeImplId<EnumKodeImpl> kodeId1a = kodeSupport.getOrCreateInstance(id1a);
        assertSame(id1, kodeId1a);
    }

    public void testNewKodeNotAllowed() {
        KodeSupportHelper kodeSupport = new TestKodeSupport(null, new TestKodelisteImplId(5));
        TestEnumKodeId id1 = new TestEnumKodeId(1, SnapshotVersion.CURRENT);

        KodeImplId<EnumKodeImpl> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        kodeSupport.setNewKoderAllowed(false);

        // Test ok to get existing codes
        TestEnumKodeId id1a = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeImplId<EnumKodeImpl> kodeId1a = kodeSupport.getOrCreateInstance(id1a);

        // Test ok to sjekk for new code
        KodeImplId<KodeImpl> KodeId = kodeSupport.getInstance(new Long(2), SnapshotVersion.CURRENT);
        assertNull(KodeId);

        // Not ok to create new codes
        TestEnumKodeId id2 = new TestEnumKodeId(2, SnapshotVersion.CURRENT);
        try {
            KodeImplId<EnumKodeImpl> kodeId2a = kodeSupport.getOrCreateInstance(id2);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }

        // Ok to create old ReplicaVersions of existing codes
        TestEnumKodeId id1_old = new TestEnumKodeId(1, SnapshotVersion.OLD);
        KodeImplId<EnumKodeImpl> kodeId1a_old = kodeSupport.getOrCreateInstance(id1_old);
        assertSame(id1_old, kodeId1a_old);
        assertEquals(kodeId1a_old.getSnapshotVersion(), SnapshotVersion.OLD);


        // Not ok to create old ReplicaVersions of new codes
        TestEnumKodeId id2_old = new TestEnumKodeId(2, SnapshotVersion.OLD);
        try {
            KodeImplId<EnumKodeImpl> kodeId2a_old = kodeSupport.getOrCreateInstance(id2_old);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }

        // ok to create old ReplicaVersions of existing codes. Will produce codes that equals current
        TestEnumKodeId id1_sv = new TestEnumKodeId(1, SnapshotVersion.createInstance("2011-10-02 08:03:15.00"));
        KodeImplId<EnumKodeImpl> kodeId1_sv = kodeSupport.getOrCreateInstance(id1_sv);
        assertSame(kodeId1, kodeId1_sv);
    }
}


abstract class KodeSupportHelper extends KodeSupport<Kodeliste, KodelisteId<Kodeliste>> {
    public KodeSupportHelper(Class<? extends KodeImplId<?>> idClass, KodelisteId kodelisteId) {
        super(idClass, kodelisteId);
    }
}
