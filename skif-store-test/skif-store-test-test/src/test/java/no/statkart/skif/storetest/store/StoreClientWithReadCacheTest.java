package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreClientReadCache;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.config.StoreTestClientWithReadCacheModule;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

@Test(groups = "singlevm-required")
public class StoreClientWithReadCacheTest extends StoreTestMixedTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    private StoreClient storeClient;
    @Inject
    StoreService storeService;

    StoreTestMockupFacade readFacade;

    public StoreClientWithReadCacheTest() {
        setModuleClass(StoreTestClientWithReadCacheModule.class);

    }

    @BeforeMethod
    protected void setUp() {
        readFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        storeClient.getRelationCache().setEnabled(false);
        storeClient.evictAll();
    }

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return ImmutableSet.of(
                        mockupFacade.getSimpleMockupFactory().getSimpleId1(),
                        mockupFacade.getSimpleMockupFactory().getSimpleId2(),
                        mockupFacade.getSimpleMockupFactory().getSimpleId3());
            }
        });
    }


    public void testStoreClientConfiguredWithReadCache() {
        assertThat(storeClient.getReadCache()).isNotNull();
    }

    public void testGetObjectLeggerObjectIReadCache() {
        SimpleId<?> simpleId1 = readFacade.getSimpleMockupFactory().getSimpleId1();
        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        storeClient.get(simpleId1);
        assertThat(readCache.get(simpleId1)).isNotNull();
    }

    public void testEvictFjernerObjektFraReadCache() {
        StoreClientReadCache readCache = storeClient.getReadCache();
        SimpleId<?> simpleId1 = readFacade.getSimpleMockupFactory().getSimpleId1();
        storeClient.get(simpleId1);
        assertThat(readCache.get(simpleId1)).isNotNull();
        storeClient.evict(simpleId1);
        assertThat(readCache.get(simpleId1)).isNull();
    }

    public void testEvictAllFjernerAlleObjekterFraReadCache() {
        StoreClientReadCache readCache = storeClient.getReadCache();
        SimpleId<?> simpleId1 = readFacade.getSimpleMockupFactory().getSimpleId1();
        SimpleId<?> simpleId2 = readFacade.getSimpleMockupFactory().getSimpleId2();
        storeClient.get(ImmutableSet.of(simpleId1, simpleId2));
        assertThat(readCache.get(simpleId1)).isNotNull();
        assertThat(readCache.get(simpleId2)).isNotNull();
        storeClient.evictAll();
        assertThat(readCache.get(simpleId1)).isNull();
        assertThat(readCache.get(simpleId2)).isNull();
    }

    public void testAbortUnitOfWorkFjerneIkkeLastetObjekterFraReadCache() {
        SimpleId<?> simpleId1 = readFacade.getSimpleMockupFactory().getSimpleId1();
        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        try (UnitOfWork unitOfWork = storeClient.beginUnitOfWork()) {
            storeClient.get(simpleId1);
            storeClient.abortUnitOfWork(unitOfWork); // For å gjøre det eksplisitt at abort kalles
        }
        assertThat(readCache.get(simpleId1)).isNotNull();
    }

    /**
     * Tester at låste objekter evictes fra readcache når endUnitOfWork kalles
     */
    public void testEndUnitOfWorkEvicterLaasteObjekterFraReadCache() {
        StoreTestMockupFacade writeFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simpleId1 = writeFacade.getSimpleMockupFactory().getSimpleId1();
        SimpleId<?> simpleId2 = writeFacade.getSimpleMockupFactory().getSimpleId2();
        SimpleId<?> simpleId3 = writeFacade.getSimpleMockupFactory().getSimpleId3();

        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        try (UnitOfWork unitOfWork = storeClient.beginUnitOfWork()) {
            Simple simple1 = storeClient.lock(simpleId1);
            storeClient.lock(simpleId2);
            storeClient.get(simpleId3);
            simple1.setText("Simple 1 v2");
            storeClient.update(simple1);

            storeClient.getUnitOfWorkTransfer(); // Simuler update på server

            assertThat(readCache.get(simpleId1)).isNotNull();
            assertThat(readCache.get(simpleId2)).isNotNull();
            assertThat(readCache.get(simpleId3)).isNotNull();
            storeClient.endUnitOfWork(unitOfWork); //
        }
        assertThat(readCache.get(simpleId1)).isNull();
        assertThat(readCache.get(simpleId2)).isNull();
        assertThat(readCache.get(simpleId3)).isNotNull();
    }

    /**
     * Tester at låste objekter evictes fra readcache når endUnitOfWork kalles
     */
    public void testEndUnitOfWorkVedNestedCommitEvicterLaasteObjekterFraReadCache() {
        StoreTestMockupFacade writeFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simpleId1 = writeFacade.getSimpleMockupFactory().getSimpleId1();
        SimpleId<?> simpleId2 = writeFacade.getSimpleMockupFactory().getSimpleId2();
        SimpleId<?> simpleId3 = writeFacade.getSimpleMockupFactory().getSimpleId3();

        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        try (UnitOfWork outer = storeClient.beginUnitOfWork()) {
            storeClient.lock(simpleId2);
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                Simple simple1 = storeClient.lock(simpleId1);
                storeClient.get(simpleId3);
                simple1.setText("Simple 1 v2");
                storeClient.update(simple1);

                storeClient.getUnitOfWorkTransfer(); // Simuler update på server

                assertThat(readCache.get(simpleId1)).isNotNull();
                assertThat(readCache.get(simpleId2)).isNotNull();
                assertThat(readCache.get(simpleId3)).isNotNull();
                storeClient.commitUnitOfWork(inner); //
            }
            storeClient.endUnitOfWork(outer);
        }
        assertThat(readCache.get(simpleId1)).isNull();
        assertThat(readCache.get(simpleId2)).isNull();
        assertThat(readCache.get(simpleId3)).isNotNull();
    }

    /**
     * Tester at låste objekter evictes fra readcache når endUnitOfWork kalles
     */
    public void testEndUnitOfWorkVedNestedAbortEvicterLaasteObjekterFraReadCache() {
        StoreTestMockupFacade writeFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simpleId1 = writeFacade.getSimpleMockupFactory().getSimpleId1();
        SimpleId<?> simpleId2 = writeFacade.getSimpleMockupFactory().getSimpleId2();
        SimpleId<?> simpleId3 = writeFacade.getSimpleMockupFactory().getSimpleId3();

        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        try (UnitOfWork outer = storeClient.beginUnitOfWork()) {
            storeClient.lock(simpleId2);
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                Simple simple1 = storeClient.lock(simpleId1);
                storeClient.get(simpleId3);
                simple1.setText("Simple 1 v2");
                storeClient.update(simple1);

                storeClient.getUnitOfWorkTransfer(); // Simuler update på server

                assertThat(readCache.get(simpleId1)).isNotNull();
                assertThat(readCache.get(simpleId2)).isNotNull();
                assertThat(readCache.get(simpleId3)).isNotNull();
                storeClient.abortUnitOfWork(inner); //
            }
            assertThat(storeClient.isLocked(simpleId1)).isFalse();
            storeClient.endUnitOfWork(outer);
        }
        assertThat(readCache.get(simpleId1)).isNotNull(); // Denne evictes ikke da endring ble abortet
        assertThat(readCache.get(simpleId2)).isNull();
        assertThat(readCache.get(simpleId3)).isNotNull();
    }

    /**
     * Denne funksjonalitet er ikke implementert for inneværende løsning. Dvs kun kall
     * via Store legger objekter i read cachen.
     */
    public void testDirekteKallTilStoreServiceLeggerIkkeObjekterIReadCache() {
        SimpleId<?> simpleId1 = readFacade.getSimpleMockupFactory().getSimpleId1();
        StoreClientReadCache readCache = storeClient.getReadCache();
        assertThat(readCache.get(simpleId1)).isNull();
        storeService.getObject(simpleId1);
        assertThat(readCache.get(simpleId1)).isNull();
    }
}
