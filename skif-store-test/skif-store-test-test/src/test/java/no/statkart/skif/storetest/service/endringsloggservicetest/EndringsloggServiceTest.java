package no.statkart.skif.storetest.service.endringsloggservicetest;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static no.statkart.skif.SkifUtil.getType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 * Tester for {@link EndringsloggService}. Her testes at kall til EndringsloggService returnerer. Mer detaljert testing
 * av endringsloggen finnes i {@link no.statkart.skif.storetest.endringslogg.EndringManagerTest}.
 *
 * Ved konstruksjon av endringslogg tester er det viktig å huske på at endrinsloggfunksjonaliteten går på tvers av test
 * datasett og rekkefølge på testsett kan varierer. Det er ikke garantert ReadTestSet kommer først.  Testene bør
 * derfor konstrueres på en slik måte at de fungere nå databasen inneholder mange test datasett.
 *
 * @author Thomas Berg
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test
public class EndringsloggServiceTest extends StoreTestTestCase {
    @Inject
    EndringsloggService endringsloggService;
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @BeforeMethod
    protected void saveReadTestset() {
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();
    }

    public void tesFindSisteEndringsnummer(){
        long endringsnr = endringsloggService.findSisteEndringsnummer(SnapshotVersion.CURRENT);
        assertTrue(endringsnr > 0);
    }

    public void testFindEndringerEtterEndringsnummer(){
        List<SimpleEndring<?>> endringer = endringsloggService.findEndringerEtterEndringsnummer(0, getType(new TypeToken<SimpleEndring<?>>() {}), 1, SnapshotVersion.CURRENT);
        assertEquals(endringer.size(),1);
    }

    public void testFindIdsEtterId(){
        List<SimpleId<?>> ids = endringsloggService.findIdsEtterId(null, Simple.class, 1, SnapshotVersion.CURRENT);
        assertEquals(ids.size(),1);
    }

    public void testCalcKontrollForRange(){
        List<SimpleEndring<?>> endringer = endringsloggService.findEndringerEtterEndringsnummer(0, getType(new TypeToken<SimpleEndring<?>>() {
        }),1,SnapshotVersion.CURRENT);
        SimpleId<?> tilId = endringer.get(0).getEndretBubbleId();
        SimpleId<?> fraId = new SimpleId<Simple>(tilId.getValue()-1);

        Kontroll kontrollRange = endringsloggService.calcKontrollForRange(fraId, tilId, Simple.class, SnapshotVersion.CURRENT);
        assertEquals(kontrollRange.getAntall(), 1);
    }

    public void testCalcKontrollForList(){
        List<SimpleEndring<?>> endringer = endringsloggService.findEndringerEtterEndringsnummer(0, getType(new TypeToken<SimpleEndring<?>>() {
        }),1,SnapshotVersion.CURRENT);
        Kontroll kontrollList = endringsloggService.calcKontrollForList(ImmutableList.of(endringer.get(0).getEndretBubbleId()), Simple.class, SnapshotVersion.CURRENT);
        assertEquals(kontrollList.getAntall(), 1);
    }



}
