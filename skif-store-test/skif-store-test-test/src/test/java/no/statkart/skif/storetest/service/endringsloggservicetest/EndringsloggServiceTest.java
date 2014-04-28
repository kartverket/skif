package no.statkart.skif.storetest.service.endringsloggservicetest;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.endringslogg.*;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.store.endringslogg.ReturnerBobler;
import no.statkart.skif.storetest.service.nedlastning.NedlastningService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


/**
 * Tester for {@link EndringsloggService}. Her testes bare at kall til EndringsloggService returnerer. Mer detaljert testing
 * av endringsloggen finnes i {@link no.statkart.skif.storetest.endringslogg.EndringManagerTest}.
 *
 * NB: Ved konstruksjon av endringslogg tester er det viktig å huske på at endrinsloggfunksjonaliteten går på tvers av test
 * datasett og at rekkefølge på testsett kan varierer. Det er ikke garantert ReadTestSet kommer før først WriteTestSet.
 * Testene bør derfor konstrueres på en slik måte at de fungere nå databasen inneholder mange test datasett med ukjendt
 * rekkefølge.
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
    NedlastningService nedlastningService;

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @BeforeMethod
    protected void saveReadTestset() {
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();
    }

    public void testFindSisteEndringId(){
        AbstractEndringId<?> sisteEndringId = endringsloggService.findSisteEndringId();
        assertNotNull(sisteEndringId);
        assertTrue(sisteEndringId.getValue() > 0);
    }

    public void testFindEndringerUtenLastingAvEndretObjekter(){
        Endringer<?> endringer = endringsloggService.findEndringer(null, Simple.class, null, ReturnerBobler.Aldri, 1);
        assertEquals(endringer.getEndringList().size(),1);
    }

    public void testFindEndringerMedLastingAvEndretObjekter() {
        Endringer<?> endringer = endringsloggService.findEndringer(null, Simple.class, null, ReturnerBobler.Alltid, 1);
        assertEquals(endringer.getEndringList().size(),1);
    }

    public void testCalcEndringskontroll(){
        Kontroll kontroll = endringsloggService.calcEndringskontroll(null, Simple.class, null, 1);
        assertEquals(kontroll.getAntall(),1);
    }

    public void testCalcObjectkontrollForList(){
        Endringer<?> endringer = endringsloggService.findEndringer(null, Simple.class, null, ReturnerBobler.Alltid, 1);
        List<SimpleId<?>> endretBubbleIds = (List)endringer.getEndretBubbleIds();
        Kontroll kontroll = endringsloggService.calcObjektkontrollForList(endretBubbleIds, Simple.class);
        assertEquals(kontroll.getAntall(),1);
    }
    public void testFindIdsEtterId(){
        List<SimpleId<?>> ids = nedlastningService.findIdsEtterId(null, Simple.class, null, 1);
        assertEquals(ids.size(),1);
    }

    public void testCalcKontrollForRange(){
        Kontroll kontrollRange = nedlastningService.calcObjektkontrollForRange(null, null, Simple.class, null);
        assertTrue(kontrollRange.getAntall() > 0);
    }

    public void testCalcKontrollForList(){
        Endringer<?> endringer = endringsloggService.findEndringer(null, Simple.class, null, ReturnerBobler.Aldri, 1);
        Kontroll kontrollList = endringsloggService.calcObjektkontrollForList(ImmutableList.of(endringer.getEndretBubbleIds().get(0)), Simple.class);
        assertEquals(kontrollList.getAntall(), 1);
    }



}
