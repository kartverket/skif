package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.basic.SimpleId;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.wsapi.mapping.testutils.DateTestContext;
import no.statkart.skif.storetest.wsapi.mapping.testutils.StoreTestMappingTestContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.TimeZone;

/**
 * Test av mapping for {@link no.statkart.skif.storetest.wsapi.domain.endringslogg.Endring}
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public class EndringMappingTest {
    private StoreTestMappingTestContext testContext;

    @BeforeMethod(alwaysRun = true)
    public void setUpTestCase() {
        testContext = new StoreTestMappingTestContext();
    }



    /**
     * SKIF-391
     * Tester mapping av {@link SnapshotVersion#CURRENT}
     */
    @Test
    public void testMapEndringstidspunktCURRENT() throws Exception {
        final DateTestContext defaultTimeContext = new DateTestContext(TimeZone.getDefault());
        final StoreTestMapping map = testContext.buildMapping();

        //generate test data
        defaultTimeContext.setLocalDate(SnapshotVersion.CURRENT.getTimestamp());

        //create wsObjectForMappingtest
        SimpleEndring wsEndring = new SimpleEndring();
        wsEndring.setEndringsnummer(1);
        wsEndring.setEndringstype(Endringstype.SLETTING);
        wsEndring.setEndretBubbleId(new SimpleId());
        wsEndring.getEndretBubbleId().setValue("22");
        wsEndring.setEndringstidspunkt(defaultTimeContext.buildXMLGregorianCalendar());

        //mapping
        no.statkart.skif.storetest.domain.endringslogg.SimpleEndring mappedEndring = map.w2d(wsEndring, no.statkart.skif.storetest.domain.endringslogg.SimpleEndring.class);

        //asserts


        //dobbel mapping
        SimpleEndring wsDoubleMappedEndring = map.d2w(mappedEndring, SimpleEndring.class); //dobbel mapping

        //asserts
        Assert.assertEquals(wsDoubleMappedEndring.getEndringsnummer(), wsEndring.getEndringsnummer(), "endringsnummer");
        Assert.assertEquals(wsDoubleMappedEndring.getEndringstype(), wsEndring.getEndringstype(), "endringstype");
        Assert.assertEquals(wsDoubleMappedEndring.getEndretBubbleId().getValue(), wsDoubleMappedEndring.getEndretBubbleId().getValue(), "endretBubble.value");
        Assert.assertEquals(wsDoubleMappedEndring.getEndretBubbleId().getClass(), wsDoubleMappedEndring.getEndretBubbleId().getClass(), "endretBubble.class");

        Assert.assertNotNull(wsDoubleMappedEndring.getEndringstidspunkt(), "forventet instans for mappet endringstidspunkt");
        Assert.assertEquals(wsDoubleMappedEndring.getEndringstidspunkt().toGregorianCalendar().getTime().toGMTString(), defaultTimeContext.date.toGMTString(), "endringstidspunkt as GMT string");
        Assert.assertEquals(wsDoubleMappedEndring.getEndringstidspunkt(), wsEndring.getEndringstidspunkt(), "endringstidspunkt");

    }




}
