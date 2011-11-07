
package no.statkart.skif.storetest.domain;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.*;
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
     * Hjelper test klasse for å kunne lage en instanse av KodelisteId som bruker en long idValue
     * @param <T>
     */
    public static class TestKodelisteId<T extends Kodeliste> extends KodelisteId<T> {
        @Override
        public Long getValue() {
            return (Long)super.getValue();
        }

        public TestKodelisteId(long value) {
            super(value);
        }
    }

    public static class TestKodeliste extends Kodeliste {}

    public static class TestKodeSupport extends KodeSupport<TestKodeliste, TestKodelisteId<TestKodeliste>> {

        public TestKodeSupport(Class<? extends KodeId<?>> idClass, TestKodelisteId<TestKodeliste> kodelisteId) {
            super(idClass, kodelisteId);
        }

        @Override
        protected <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public void testGetKodeListeId() {
        TestKodeSupport kodeSupport = new TestKodeSupport(null, new TestKodelisteId(5));
        KodelisteId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));
    }

    public void testGetKodeIdClass() {
        TestKodeSupport kodeSupport = new TestKodeSupport(null, new TestKodelisteId(5));
        assertEquals(kodeSupport.getKodeIdClass(), null);
    }
}
