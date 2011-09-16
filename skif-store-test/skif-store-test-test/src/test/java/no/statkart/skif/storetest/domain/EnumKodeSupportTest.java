package no.statkart.skif.storetest.domain;

import no.statkart.skif.storetest.domain.kodeliste.Kodeliste;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeSupport;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class EnumKodeSupportTest {

    public void testCreate() {
        EnumKodeSupport kodeSupport = new EnumKodeSupport(null, 5, "Test");
        KodelisteId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

    public void testAdd() {
        EnumKodeSupport kodeSupport = new EnumKodeSupport(null, 5, "Test");
        KodelisteId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

}
