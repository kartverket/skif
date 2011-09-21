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
        EnumKodeSupportHelper2 kodeSupport = new EnumKodeSupportHelper2(null, 5, "Test");
        KodelisteId2 id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste2 kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

    public void testAdd() {
        EnumKodeSupportHelper2 kodeSupport = new EnumKodeSupportHelper2(null, 5, "Test");
        KodelisteId2 id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        Kodeliste2 kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

}

// Hjelperklasse for ikke å skulle skrive så mye
final class EnumKodeSupportHelper2 extends EnumKodeSupport2<EnumKodeliste2, EnumKodelisteId2<EnumKodeliste2>> {
    public EnumKodeSupportHelper2(Class<? extends EnumKodeIdImpl2<?>> idClass, long kodelisteIdValue, String kodelisteNavn) {
        super(idClass, new EnumKodelisteIdImpl2(kodelisteIdValue), kodelisteNavn);
    }
}
