package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

/**
 * Tester for EnumKodelisteManager
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class EnumKodelisteManagerTest {

    public void createEnumKodeManager() {
        EnumKodelisteManager enumKodeManager = new EnumKodelisteManager();
        enumKodeManager.installStatic(AEnumKodeId.class);
        enumKodeManager.installStatic(BEnumKodeId.class);

        Kodeliste aKodeliste = enumKodeManager.get(AEnumKodeId.KODELISTE_ID);
        assertThat(aKodeliste.getKodeIds()).containsExactly(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId, AEnumKodeId.KodeBId);

        AEnumKode aEnumKode_A = enumKodeManager.get(AEnumKodeId.KodeAId);
        assertEquals(aEnumKode_A.getId(),AEnumKodeId.KodeAId );
        assertEquals(aEnumKode_A.getKodelisteId(),AEnumKodeId.KODELISTE_ID);

        Kodeliste aKodeliste_2 = enumKodeManager.get(AEnumKodeId.KODELISTE_ID);
        assertEquals(aKodeliste, aKodeliste_2);
        assertNotSame(aKodeliste, aKodeliste_2);
    }

}
