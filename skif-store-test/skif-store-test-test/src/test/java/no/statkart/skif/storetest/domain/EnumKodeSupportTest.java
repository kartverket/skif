package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.kodelistesupport.*;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class EnumKodeSupportTest {

    public void testCreate() {
        EnumKodeSupportHelper kodeSupport = new EnumKodeSupportHelper(null, 5, "Test");
        KodelisteImplId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        KodelisteImpl kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

    public void testAdd() {
        EnumKodeSupportHelper kodeSupport = new EnumKodeSupportHelper(null, 5, "Test");
        KodelisteImplId id = kodeSupport.getKodelisteId();
        assertNotNull(id);
        assertEquals(id.getValue(), new Long(5));

        KodelisteImpl kodeliste = kodeSupport.getNonLocalizedKodeliste();
        assertNotNull(kodeliste);
        assertEquals(kodeliste.getNavn(), "Test");
    }

}

// Hjelperklasse for ikke å skulle skrive så mye
final class EnumKodeSupportHelper extends EnumKodeSupport<EnumKodelisteImpl, EnumKodelisteImplId<EnumKodelisteImpl>> {
    public EnumKodeSupportHelper(Class<? extends EnumKodeImplId<?>> idClass, long kodelisteIdValue, String kodelisteNavn) {
        super(idClass, new TestEnumKodelisteId(kodelisteIdValue), kodelisteNavn);
    }
}


