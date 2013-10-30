package no.statkart.skif.storetest.service.endringsloggservicetest;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;


/**
 * @author Thomas Berg
 */
@Test
public class EndringsloggServiceTest extends StoreTestTestCase {
    public void testEndringsloggService(){
    final EndringsloggService endringsloggService = injector.getInstance(EndringsloggService.class);
        long endringsnr = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);
        List<SimpleEndring> endringer = endringsloggService.findEndringerEtterEndringsnummer(1L,SimpleEndring.class,1,SnapshotVersion.CURRENT);
        assertEquals(endringer.size(),1);
    }
}
