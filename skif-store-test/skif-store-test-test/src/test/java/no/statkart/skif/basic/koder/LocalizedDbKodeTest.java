package no.statkart.skif.basic.koder;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKode;
import no.statkart.skif.storetest.domain.koder.SimpleLocalizedDbKodeId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * Tester lokalisering av databasekoder.
 */
public class LocalizedDbKodeTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private TestdataService testdataService;

    @Inject
    private StoreService storeService;

    public void testUpdateDbKodeRootLocale() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();
        TestNumber testNumber = mockupFacade.getTestNumber();
        final Locale norsk = new Locale("NO", "no");

        SimpleLocalizedDbKodeId kodeId = mockupFacade.getIdService().getNextId(SimpleLocalizedDbKodeId.class);

        SimpleLocalizedDbKode testKode = new SimpleLocalizedDbKode();
        testKode.setId(kodeId);
        testKode.setKodeverdi("test" + testNumber.getNumber());
        LocalizedString beskrivelse = new LocalizedString();
        beskrivelse.setText(Locale.ROOT, "Test" + testNumber.getNumber());
        testKode.setBeskrivelse(beskrivelse);
        mockupFacade.getStore().insert(testKode);

        testdataService.saveAll(mockupFacade.getAllTransfers());

        SimpleLocalizedDbKode insertedKode = storeService.getObject(kodeId);
        Assert.assertEquals(insertedKode.getBeskrivelse().getText(Locale.ROOT), "Test" + testNumber.getNumber(), "Inserted beskrivelse ROOT");
        Assert.assertEquals(insertedKode.getBeskrivelse().getText(norsk), "Test" + testNumber.getNumber(), "Inserted beskrivelse norsk");

        beskrivelse = insertedKode.getBeskrivelse();
        beskrivelse.setText(Locale.ROOT, "Updated" + testNumber.getNumber());
        beskrivelse.setText(norsk, "Oppdatert" + testNumber.getNumber());
        insertedKode.setBeskrivelse(beskrivelse);
        testdataService.saveSnapshotTransfer(SnapshotVersion.CURRENT, new MockupTransfer(Collections.<BubbleObject>emptyList(), Arrays.asList(insertedKode), Collections.<BubbleObject>emptyList(), testNumber));

        SimpleLocalizedDbKode updatedKode = storeService.getObject(kodeId);
        Assert.assertEquals(insertedKode.getBeskrivelse().getText(Locale.ROOT), "Updated" + testNumber.getNumber(), "Updated beskrivelse ROOT");
        Assert.assertEquals(insertedKode.getBeskrivelse().getText(norsk), "Oppdatert" + testNumber.getNumber(), "Updated beskrivelse norsk");
    }
}
