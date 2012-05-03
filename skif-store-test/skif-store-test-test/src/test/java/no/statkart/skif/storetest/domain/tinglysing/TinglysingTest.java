package no.statkart.skif.storetest.domain.tinglysing;

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
            final HjemmelForPerson hjemmelForPerson = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getHjemmelForPersonMockupFactory().getId_26288865());
//            final AndelIMatrikkelenhet andelIMatrikkelenhet = readFacadeTinglysing.getStore().get(hjemmelForPerson.getKjoeptAndelIds().iterator().next());
//            final Person person = readFacadeTinglysing.getStore().get(andelIMatrikkelenhet.getAndelseierPersonId());
//            assertTrue(person.getNavn().equalsIgnoreCase("FLUSUND BEATE PAULSEN")||person.getNavn().equalsIgnoreCase("SELJESET DAG JOHNNY") );
        }
    }

}
