package no.statkart.skif.standalone.store.persistence.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.persistence.kodeliste.EnumKodelisteManager;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester for EnumKodelisteManager
 *
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
        assertThat(aKodeliste.getKoderIds()).containsExactly(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId, AEnumKodeId.KodeBId);

        AEnumKode aEnumKode_A = enumKodeManager.get(AEnumKodeId.KodeAId);
        assertThat(aEnumKode_A.getId()).isEqualTo(AEnumKodeId.KodeAId);
        assertThat(aEnumKode_A.getKodelisteId()).isEqualTo(AEnumKodeId.KODELISTE_ID);

        Kodeliste aKodeliste_2 = enumKodeManager.get(AEnumKodeId.KODELISTE_ID);
        assertThat(aKodeliste).isEqualTo(aKodeliste_2).isNotSameAs(aKodeliste_2);
    }

}
