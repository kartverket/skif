package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.Beloep;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Christian Rørdam
 * @since 2.1
 */
@Test
public class TinglysingTest extends StoreTestTestCase {

    public void test() {
        TinglysingMockupFacadeBuilder tinglysingMockupFacadeBuilder = injector.getInstance(TinglysingMockupFacadeBuilder.class);
        TinglysingMockupFacade readFacadeTinglysing = tinglysingMockupFacadeBuilder.getForReadTest();
        {
            final Matrikkelenhet matrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getMatrikkelenhetMockupFactory().getId_0412_742_78_0_0());
            final Kommune kommune = readFacadeTinglysing.getStore().get(matrikkelenhet.getKommuneId());
            assertEquals(kommune.getKommunenummer(), "0412");
        }
        {
            final Person person = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getPersonMockupFactory().getId_29107235289());
            assertEquals(person.getNavn(), "FLUSUND BEATE PAULSEN");
        }
        {
            final Dokument dokument = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getDokumentMockupFactory().getId_100394_200_2010_2());
            assertEquals(dokument.getDokumentnummer(), 100394);
        }
        {
            final NivaaIMatrikkelenhet nivaaIMatrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getNivaaIMatrikkelenhetMockupFactory().getId_1449_58_13_0_0_F1());
            final Matrikkelenhet matrikkelenhet = readFacadeTinglysing.getStore().get(nivaaIMatrikkelenhet.getMatrikkelenhetId());
            final Kommune kommune = readFacadeTinglysing.getStore().get(matrikkelenhet.getKommuneId());
            assertEquals(kommune.getKommunenummer(), "1449");
        }
        {
            final AndelIMatrikkelenhet andelIMatrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getAndelIMatrikkelenhetMockupFactory().getId_4004000_11_2());
            final Person person = readFacadeTinglysing.getStore().get(andelIMatrikkelenhet.getAndelseierPersonId());
            assertEquals(person.getNavn(), "FLUSUND BEATE PAULSEN");
        }
        {
            // id_26288865, FE_FES
            final HjemmelForPerson hjemmelForPerson = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getHjemmelForPersonMockupFactory().getId_26288865());
            final AndelIMatrikkelenhet andelIMatrikkelenhet = readFacadeTinglysing.getStore().get(hjemmelForPerson.getKjoeptAndelIds().iterator().next());
            final Person person = readFacadeTinglysing.getStore().get(andelIMatrikkelenhet.getAndelseierPersonId());
            assertTrue(person.getNavn().equalsIgnoreCase("FLUSUND BEATE PAULSEN") || person.getNavn().equalsIgnoreCase("SELJESET DAG JOHNNY"));
            final Beloep beloep = hjemmelForPerson.getVederlag();
            assertTrue(beloep.getValuta().equalsIgnoreCase("NOK"));
            assertTrue(beloep.getBeloepsverdi().longValue() == 0);
        }
        {
            // id_36405749, JS_JSA
            final HjemmelForMatrikkelenhet hjemmelForMatrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getHjemmelForMatrikkelenhetMockupFactory().getId_36405749());
            assertTrue(hjemmelForMatrikkelenhet.getRettsstiftelsestype().equals(RettsstiftelsestypeKodeId.JS_JSA));
            final Dokument dokument = readFacadeTinglysing.getStore().get(hjemmelForMatrikkelenhet.getDokumentId());
            assertTrue(dokument.getDokumentaar() == 2010);
            final AndelIMatrikkelenhet andelIMatrikkelenhet = readFacadeTinglysing.getStore().get(hjemmelForMatrikkelenhet.getNyeAndelIds().iterator().next());
            final NivaaIMatrikkelenhet nivaaIMatrikkelenhet = readFacadeTinglysing.getStore().get(andelIMatrikkelenhet.getAndelseierNivaaIMatrikkelenhetId());
            assertTrue(nivaaIMatrikkelenhet.getMatrikkelenhetsnivaaKodeId().equals(MatrikkelenhetsnivaaKodeId.Grunn));
        }
        {
            // id_33124569, FA_FAR
            final HjemmelForMatrikkelenhet hjemmelForMatrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getHjemmelForMatrikkelenhetMockupFactory().getId_33124569());
            assertTrue(hjemmelForMatrikkelenhet.getRettsstiftelsestype().equals(RettsstiftelsestypeKodeId.FA_FAR));
            final Dokument dokument = readFacadeTinglysing.getStore().get(hjemmelForMatrikkelenhet.getDokumentId());
            assertTrue(dokument.getDokumentaar() == 2007);
            boolean nivaaIMatrikkelenhetFunnet = false;
            for (AndelIMatrikkelenhet andelIMatrikkelenhet : readFacadeTinglysing.getStore().get(hjemmelForMatrikkelenhet.getNyeAndelIds())) {
                final NivaaIMatrikkelenhet nivaaIMatrikkelenhet = readFacadeTinglysing.getStore().get(andelIMatrikkelenhet.getAndelseierNivaaIMatrikkelenhetId());
                final Matrikkelenhet matrikkelenhet = readFacadeTinglysing.getStore().get(nivaaIMatrikkelenhet.getMatrikkelenhetId());
                nivaaIMatrikkelenhetFunnet = (nivaaIMatrikkelenhetFunnet || (matrikkelenhet.getGaardsnummer() == 57 && matrikkelenhet.getBruksnummer() == 10 && matrikkelenhet.getFestenummer() == 2 && nivaaIMatrikkelenhet.getMatrikkelenhetsnivaaKodeId().equals(MatrikkelenhetsnivaaKodeId.Feste)));
            }
            assertTrue(nivaaIMatrikkelenhetFunnet);
        }
    }

}
