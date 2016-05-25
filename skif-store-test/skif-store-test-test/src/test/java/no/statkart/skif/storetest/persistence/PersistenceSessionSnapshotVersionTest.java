package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.BubbleWithKode;
import no.statkart.skif.storetest.domain.basic.BubbleWithKodeId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "singlevm-required")
public class PersistenceSessionSnapshotVersionTest extends StoreTestServerTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store store;

    public void testBubbleIdType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        BubbleWithKodeId<?> idCurrent = mockupFacade.getBubbleWithKodeMockupFactory().getBubbleWithKodeId1();
        BubbleWithKodeId<?> idOld = idCurrent.asSnapshotVersionOld();

        BubbleWithKode objectCurrent = store.get(idCurrent);
        BubbleWithKode objectOld = store.get(idOld);

        Assert.assertEquals(objectCurrent.getId().getSnapshotVersion(), SnapshotVersion.CURRENT, "Current");
        Assert.assertEquals(objectOld.getId().getSnapshotVersion(), SnapshotVersion.OLD, "Old");
    }

    public void testEnumKodeIdType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        BubbleWithKodeId<?> idCurrent = mockupFacade.getBubbleWithKodeMockupFactory().getBubbleWithKodeId1();
        BubbleWithKodeId<?> idOld = idCurrent.asSnapshotVersionOld();
        BubbleWithKodeId<?> idHist = idCurrent.asSnapshotVersion(SnapshotVersion.START); // I SKIF så kjøres historikk på OLD-session

        BubbleWithKode objectCurrent = store.get(idCurrent);
        BubbleWithKode objectOld = store.get(idOld);
        BubbleWithKode objectHist = store.get(idHist);

        Assert.assertEquals(objectCurrent.getTestAEnumKodeId().getSnapshotVersion(), SnapshotVersion.CURRENT, "Current");
        Assert.assertEquals(objectOld.getTestAEnumKodeId().getSnapshotVersion(), SnapshotVersion.OLD, "Old"); // SKIF-577
        Assert.assertEquals(objectHist.getTestAEnumKodeId().getSnapshotVersion(), SnapshotVersion.START, "Historikk"); // SKIF-576
    }
}
