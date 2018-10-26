package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static no.statkart.skif.storetest.mockup.MockupSnapshots.CURRENT;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S0;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S1;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S2;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S3;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S4;
import static no.statkart.skif.storetest.mockup.MockupSnapshots.S4_justafter;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test
public class StoreHistoricVersionTest extends StoreTestTestCase {
    @Inject
    StoreService service;
    @Inject
    Store store;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    SnapshotVersionContext snapshotVersionContext;

    public void testFindHistSimpleIdsForIntervalSomInneholderAlleHeltTilCurrent() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        Collection<HistSimpleId<?>> ids1 = service.<HistSimpleId<?>>getVersions(histSimpleId1, S0, CURRENT);
        Assert.assertEquals(ids1.size(), 5);
        assertThat(ids1).containsExactly(
                histSimpleId1.asSnapshotVersion(S0),
                histSimpleId1.asSnapshotVersion(S1),
                histSimpleId1.asSnapshotVersion(S2),
                histSimpleId1.asSnapshotVersion(S3),
                histSimpleId1.asSnapshotVersion(S4));
    }

    public void testFindHistSimpleIdsForIntervalSomInneholderAlleHeltTilCurrentUsingOldId() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final HistSimpleId<?> histSimpleId1Old = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1().asSnapshotVersionOld();
        Collection<HistSimpleId<?>> ids1;
        SnapshotVersion orgSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            snapshotVersionContext.setSnapshotVersion(histSimpleId1Old.getSnapshotVersion());
            ids1 = service.<HistSimpleId<?>>getVersions(histSimpleId1Old, S0, CURRENT);
        } finally {
            snapshotVersionContext.setSnapshotVersion(orgSnapshotVersion);
        }
        Assert.assertEquals(ids1.size(), 5);
        assertThat(ids1).containsExactly(
                histSimpleId1Old.asSnapshotVersion(S0),
                histSimpleId1Old.asSnapshotVersion(S1),
                histSimpleId1Old.asSnapshotVersion(S2),
                histSimpleId1Old.asSnapshotVersion(S3),
                histSimpleId1Old.asSnapshotVersion(S4));

    }

    public void testFindBubbleIdsForIntervalSomInneholderAkkuratAlle() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<HistSimpleId<?>> ids1 = service.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), S0, S4_justafter);
        Assert.assertEquals(ids1.size(), 5);
    }


    public void testFindBubbleIdsForIntervalSomIkkeInneholderFoerste() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<HistSimpleId<?>> ids1 = service.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), S1, CURRENT);
        Assert.assertEquals(ids1.size(), 4);
    }

    public void testFindBubbleIdsForIntervalSomBareInneholderFoerste() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<HistSimpleId<?>> ids1 = service.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), S1, S2);
        Assert.assertEquals(ids1.size(), 1);
    }

    public void testFindBubbleIdsForIntervalSomErTomt() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<HistSimpleId<?>> ids1 = service.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), S1, S1);
        Assert.assertEquals(ids1.size(), 0);
        Collection<HistSimpleId<?>> ids2 = service.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), CURRENT, CURRENT);
        Assert.assertEquals(ids2.size(), 0);
    }

    public void testFindBubbleIdsForIntervalSomInneholderAlleHeltTilCurrent_List() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final List<HistSimpleId<?>> ids = ImmutableList.of(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2());
        final Map<HistSimpleId<?>, List<HistSimpleId<?>>> versionsForList = service.getVersionsForList(ids, S0, CURRENT);
        assertThat(versionsForList).hasSize(2);
        assertThat(versionsForList).containsKey(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1());
        assertThat(versionsForList).containsKey(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2());
        assertThat(versionsForList.get(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1())).hasSize(5);
        assertThat(versionsForList.get(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2())).hasSize(2);
    }


    public void testGetVersionsViaStore() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<HistSimpleId<?>> ids1 = store.<HistSimpleId<?>>getVersions(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), S0, CURRENT);
        Assert.assertEquals(ids1.size(), 5);
    }

    public void testGetVersionsViaStore_List() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final HistSimpleId<?> histSimpleId1 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1();
        final HistSimpleId<?> histSimpleId2 = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2();
        final List<HistSimpleId<?>> ids = ImmutableList.of(histSimpleId1, histSimpleId2);
        final Map<HistSimpleId<?>, List<HistSimpleId<?>>> versionsForList = store.getVersionsForList(ids, S0, CURRENT);
        assertThat(versionsForList).hasSize(2);
        assertThat(versionsForList.get(histSimpleId1)).containsExactly(
                histSimpleId1.asSnapshotVersion(S0),
                histSimpleId1.asSnapshotVersion(S1),
                histSimpleId1.asSnapshotVersion(S2),
                histSimpleId1.asSnapshotVersion(S3),
                histSimpleId1.asSnapshotVersion(S4));
        assertThat(versionsForList.get(histSimpleId2)).containsExactly(
                histSimpleId2.asSnapshotVersion(S2),
                histSimpleId2.asSnapshotVersion(S3));
    }

    /**
     * Tester at returnert map alltid bruker SnapshotVersion.CURRENT som keys uansett hvilken snapshot version som
     * søke id'ene bruker. Denne test bruker SnapshotVersion.OLD som innput for søkeid, men må hente ut resultatet med id'er
     * som har SnapshotVersion.CURRENT
     *
     */
    public void testGetVersionsViaStore_ListUsingOld() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final HistSimpleId<?> histSimpleId1Old = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1().asSnapshotVersionOld();
        final HistSimpleId<?> histSimpleId2Old = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2().asSnapshotVersionOld();
        final List<HistSimpleId<?>> ids = ImmutableList.of(histSimpleId1Old, histSimpleId2Old);
        final Map<HistSimpleId<?>, List<HistSimpleId<?>>> versionsForList;
        final SnapshotVersion orgSnapshotVersion = snapshotVersionContext.getSnapshotVersion();
        try {
            snapshotVersionContext.setSnapshotVersion(SnapshotVersion.OLD);
            versionsForList = store.getVersionsForList(ids, S0, CURRENT);
        } finally {
            snapshotVersionContext.setSnapshotVersion(orgSnapshotVersion);
        }
        assertThat(versionsForList).hasSize(2);
        assertThat(versionsForList.get(histSimpleId1Old.asSnapshotVersionOld())).containsExactly(
                histSimpleId1Old.asSnapshotVersion(S0),
                histSimpleId1Old.asSnapshotVersion(S1),
                histSimpleId1Old.asSnapshotVersion(S2),
                histSimpleId1Old.asSnapshotVersion(S3),
                histSimpleId1Old.asSnapshotVersion(S4));
        assertThat(versionsForList.get(histSimpleId2Old.asSnapshotVersionOld())).containsExactly(
                histSimpleId2Old.asSnapshotVersion(S2),
                histSimpleId2Old.asSnapshotVersion(S3));
    }
}
