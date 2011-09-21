package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.kodelistesupport2.*;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class EnumKodeSupportTest2 {

    public void testCreate() {
        EnumKodeSupport2 kodeSupport = new EnumKodeSupport2(null, 5, "Test");
        KodelisteId2 id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste2 kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

    public void testAdd() {
        EnumKodeSupport2 kodeSupport = new EnumKodeSupport2(null, 5, "Test");
        KodelisteId2 id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste2 kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

}
