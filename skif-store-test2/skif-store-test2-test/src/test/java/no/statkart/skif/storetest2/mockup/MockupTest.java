package no.statkart.skif.storetest2.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.service.store.StoreService;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Tester at mockups virker.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class MockupTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Test(invocationCount = 2) // Kjør to ganger for å teste at readset bare forsøkes skrives ned én gang
    public void readSet() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        Eier eier = storeService.getObject(mockupFacade.getEierMockupFactory().getEier1Id());

        Assert.assertEquals(eier.getId(), mockupFacade.getEierMockupFactory().getEier1Id());
        Assert.assertEquals(eier.getEiendommerIdsSet(), mockupFacade.getStore().get(mockupFacade.getEierMockupFactory().getEier1Id()).getEiendommerIdsSet());
    }

    public void writeSet() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();

        Eier eier = storeService.getObject(mockupFacade.getEierMockupFactory().getEier1Id());

        Assert.assertEquals(eier.getId(), mockupFacade.getEierMockupFactory().getEier1Id());
        Assert.assertEquals(eier.getEiendommerIdsSet(), mockupFacade.getStore().get(mockupFacade.getEierMockupFactory().getEier1Id()).getEiendommerIdsSet());
    }

    public void getForEiendomId() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacade();
        MockupTransfer transferForIds = mockupFacade.getTransferForIds(ImmutableSet.of(mockupFacade.getEiendomMockupFactory().getEiendom1Id()));

        Set<BubbleObject> expected = new HashSet<BubbleObject>();
        expected.add(mockupFacade.getStore().get(mockupFacade.getEiendomMockupFactory().getEiendom1Id()));

        Assert.assertEquals(transferForIds.getInsertedObjects(), expected);
    }

    public void getForEierId() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacade();
        MockupTransfer transferForIds = mockupFacade.getTransferForIds(ImmutableSet.of(mockupFacade.getEierMockupFactory().getEier1Id()));

        Set<BubbleObject> expected = new HashSet<BubbleObject>();
        expected.add(mockupFacade.getStore().get(mockupFacade.getEierMockupFactory().getEier1Id()));
        expected.add(mockupFacade.getStore().get(mockupFacade.getEiendomMockupFactory().getEiendom1Id()));
        expected.add(mockupFacade.getStore().get(mockupFacade.getEiendomMockupFactory().getEiendom2Id()));

        Assert.assertEquals(transferForIds.getInsertedObjects(), expected);
    }
}
