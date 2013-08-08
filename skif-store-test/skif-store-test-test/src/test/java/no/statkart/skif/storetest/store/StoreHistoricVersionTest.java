package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.mockup.MockupSnapshots;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test(enabled = true)
public class StoreHistoricVersionTest extends StoreTestTestCase {
    @Inject
    StoreService service;
    @Inject
    Store store;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    public void testFindBubbleIdsForIntervalSomInneholderAlleHeltTilCurrent() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = service.getVersions(histSimpleId1, MockupSnapshots.S0, SnapshotVersion.CURRENT);
        Assert.assertEquals(versions.size(), 5);
    }


    public void testFindBubbleIdsForIntervalSomInneholderAkkuratAlle() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = service.getVersions(histSimpleId1, MockupSnapshots.S0, MockupSnapshots.S4_justafter);
        Assert.assertEquals(versions.size(), 5);
    }


    public void testFindBubbleIdsForIntervalSomIkkeInneholderFoerste() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = service.getVersions(histSimpleId1, MockupSnapshots.S1, MockupSnapshots.S4_justafter);
        Assert.assertEquals(versions.size(), 4);

    }

    public void testFindBubbleIdsForIntervalSomBareInneholderFoerste() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = service.getVersions(histSimpleId1, MockupSnapshots.S0, MockupSnapshots.S1);
        Assert.assertEquals(versions.size(), 1);
        Assert.assertEquals(versions.iterator().next().getSnapshotVersion(), MockupSnapshots.S0);
    }

    public void testFindBubbleIdsForIntervalSomErTomt() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = service.getVersions(histSimpleId1, MockupSnapshots.S1, MockupSnapshots.S1);
        Assert.assertEquals(versions.size(), 0);
    }

    public void testFindBubbleIdsForIntervalSomInneholderAlleHeltTilCurrent_List() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        List<HistSimpleId<?>> histSimpleIds = Arrays.asList(
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(),
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2()
        );

        Map<HistSimpleId<?>, List<HistSimpleId<?>>> versionsForList = service.getVersionsForList(histSimpleIds, MockupSnapshots.S0, SnapshotVersion.CURRENT);
        Assert.assertEquals(versionsForList.size(), 2);
        Assert.assertNotNull(versionsForList.get(histSimpleIds.get(0)), "Fant ikke versjoner hørende til" + histSimpleIds.get(0));
        Assert.assertNotNull(versionsForList.get(histSimpleIds.get(1)), "Fant ikke versjoner hørende til" + histSimpleIds.get(1));
        Assert.assertEquals(versionsForList.get(histSimpleIds.get(0)).size(), 5);
        Assert.assertEquals(versionsForList.get(histSimpleIds.get(1)).size(), 2);
    }


    public void testGetVersionsViaStore() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        List<? extends HistSimpleId<?>> versions = store.getVersions(histSimpleId1, MockupSnapshots.S0, SnapshotVersion.CURRENT);
        Assert.assertEquals(versions.size(), 5);
    }

    public void testGetVersionsViaStore_List() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        List<HistSimpleId<?>> histSimpleIds = Arrays.asList(
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(),
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2()
        );

        Map<HistSimpleId<?>, List<HistSimpleId<?>>> versionsForList = store.getVersionsForList(histSimpleIds, MockupSnapshots.S0, SnapshotVersion.CURRENT);
        Assert.assertEquals(versionsForList.size(), 2);
        Assert.assertNotNull(versionsForList.get(histSimpleIds.get(0)), "Fant ikke versjoner hørende til" + histSimpleIds.get(0));
        Assert.assertNotNull(versionsForList.get(histSimpleIds.get(1)), "Fant ikke versjoner hørende til" + histSimpleIds.get(1));
        Assert.assertEquals(versionsForList.get(histSimpleIds.get(0)).size(), 5);
        Assert.assertEquals(versionsForList.get(histSimpleIds.get(1)).size(), 2);
      }
}
