package no.statkart.skif.storetest.domain;


import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.BubbleKode;
import no.statkart.skif.store.kodelistesupport.BubbleKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.Kode;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKode;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.impl.KodeSupport;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
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
    public static class TestEnumKodeId extends EnumKodeId<EnumKode> {
        public TestEnumKodeId(long value, SnapshotVersion snapshotVersion) {
            super(new Long(value), snapshotVersion);
        }

        @Override
        protected EnumKodeSupport getKodeSupport() {
            return null;
        }
    }

    public static class TestKodeSupport extends KodeSupport {
        public TestKodeSupport(Class<? extends KodeId<?>> idClass, KodelisteId kodelisteId) {
            super(idClass, kodelisteId);
        }

        @Override
        protected <T extends BubbleKode> String getBeskrivelse(T kode, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
        @Override
        protected <T extends BubbleKodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    public void testKodeListeid() {
        KodeSupport kodeSupport = new TestKodeSupport(null, new KodelisteId(5));
        KodelisteId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));
    }

    public void testCreateKodeId() {
        KodeSupport kodeSupport = new TestKodeSupport(null, new KodelisteId(5));
        KodeId<Kode> id = kodeSupport.getInstance(new Long(1), SnapshotVersion.CURRENT);
        assertNull(id);

        TestEnumKodeId id1 = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeId<EnumKode> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        assertSame(id1, kodeId1);

        TestEnumKodeId id1a = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeId<EnumKode> kodeId1a = kodeSupport.getOrCreateInstance(id1a);
        assertSame(id1, kodeId1a);
    }

    public void testNewKodeNotAllowed() {
        KodeSupport kodeSupport = new TestKodeSupport(null, new KodelisteId(5));
        TestEnumKodeId id1 = new TestEnumKodeId(1, SnapshotVersion.CURRENT);

        KodeId<EnumKode> kodeId1 = kodeSupport.getOrCreateInstance(id1);
        kodeSupport.setNewKoderAllowed(false);

        // Test ok to get existing codes
        TestEnumKodeId id1a = new TestEnumKodeId(1, SnapshotVersion.CURRENT);
        KodeId<EnumKode> kodeId1a = kodeSupport.getOrCreateInstance(id1a);

        // Test ok to sjekk for new code
        KodeId<Kode> kodeId2 = kodeSupport.getInstance(new Long(2), SnapshotVersion.CURRENT);
        assertNull(kodeId2);

        // Not ok to create new codes
        TestEnumKodeId id2 = new TestEnumKodeId(2, SnapshotVersion.CURRENT);
        try {
            KodeId<EnumKode> kodeId2a = kodeSupport.getOrCreateInstance(id2);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }

        // Ok to create old SnapshotVersions of existing codes
        TestEnumKodeId id1_old = new TestEnumKodeId(1, SnapshotVersion.OLD);
        KodeId<EnumKode> kodeId1a_old = kodeSupport.getOrCreateInstance(id1_old);
        assertSame(id1_old, kodeId1a_old);


        // Not ok to create old SnapshotVersions of new codes
        TestEnumKodeId id2_old = new TestEnumKodeId(2, SnapshotVersion.OLD);
        try {
            KodeId<EnumKode> kodeId2a_old = kodeSupport.getOrCreateInstance(id2_old);
            fail("Expected exception");
        } catch (ImplementationException e) {
        }
    }
}
