package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Rørdam
 * @since 2.1
 */
@Test
public class TinglysingTest extends StoreTestTestCase {

    public void test() {
        TinglysingMockupFacadeBuilder tinglysingMockupFacadeBuilder = injector.getInstance(TinglysingMockupFacadeBuilder.class);
        TinglysingMockupFacade readFacadeTinglysing = tinglysingMockupFacadeBuilder.getForReadTest();
        final Matrikkelenhet matrikkelenhet = readFacadeTinglysing.getStore().get(readFacadeTinglysing.getMatrikkelenhetMockupFactory().getId_0412_742_78_0_0());
        final Kommune kommune = readFacadeTinglysing.getStore().get(matrikkelenhet.getKommuneId());
        assertEquals(kommune.getKommunenummer(), "0412");
    }

}
