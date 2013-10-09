package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.storetest.domain.relation.X1AAMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1BBOneMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1CCManyMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class UnidirectionalWithoutComponentsTest extends StoreTestTestCase {
    @Inject
    Store store;
    @Inject
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;


    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return Sets.union(
                        Sets.union(
                                mockupFacade.getX1AAMockupFactory().getAllIds(X1AAId.class),
                                mockupFacade.getX1BBOneMockupFactory().getAllIds(X1BBOneId.class)
                        ),
                        mockupFacade.getX1CCManyMockupFactory().getAllIds(X1CCManyId.class)
                );
            }
        });
    }

    public void testManyToOneMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getSomeBBId(), x1BBOneMockupFactory.getB2Id());

        // Sjekk at a2 og a3 peker på samme B
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        assertNotNull(a2);
        assertEquals(a2.getSomeBBId(), x1BBOneMockupFactory.getB3Id());

        X1AA a3 = store.get(x1AAMockupFactory.getA2Id());
        assertNotNull(a3);
        assertEquals(a3.getSomeBBId(), x1BBOneMockupFactory.getB3Id());
    }

    public void testGetInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
        Set<X1AAId<?>> b1InvSomeBBIds = b1.findInvSomeBBIds();
        assertThat(b1InvSomeBBIds).isEmpty();

        X1BBOne b2 = store.get(x1BBOneMockupFactory.getB2Id());
        Set<X1AAId<?>> b2InvSomeBBIds = b2.findInvSomeBBIds();
        assertThat(b2InvSomeBBIds).containsOnly(x1AAMockupFactory.getA1Id());

        X1BBOne b3 = store.get(x1BBOneMockupFactory.getB3Id());
        Set<X1AAId<?>> b3InvSomeBBIds = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }


    public void testGetCachedInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        ImmutableSet<X1BBOneId<?>> bbIds = ImmutableSet.of(x1BBOneMockupFactory.getB1Id(), x1BBOneMockupFactory.getB2Id(), x1BBOneMockupFactory.getB3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        store.get(bbIds);
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(bbIds);


        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
        Set<X1AAId<?>> b1InvSomeBBIds = b1.findInvSomeBBIds();
        assertThat(b1InvSomeBBIds).isEmpty();

        X1BBOne b2 = store.get(x1BBOneMockupFactory.getB2Id());
        Set<X1AAId<?>> b2InvSomeBBIds = b2.findInvSomeBBIds();
        assertThat(b2InvSomeBBIds).containsOnly(x1AAMockupFactory.getA1Id());

        X1BBOne b3 = store.get(x1BBOneMockupFactory.getB3Id());
        Set<X1AAId<?>> b3InvSomeBBIds = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());

        Set<X1AAId<?>> b3InvSomeBBIds2 = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds2).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }

    public void testChangeSomeBBRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
        assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());

        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.setSomeBBId(b1.getId());
        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
    }

    public void testGetMany() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();

        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertThat(a1.getSomeCCsIds()).hasSize(0);

        X1AA a3 = store.get(x1AAMockupFactory.getA3Id());
        assertThat(a3.getSomeCCsIds()).hasSize(2);
        assertThat(a3.getSomeCCsIds()).containsOnly(x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id());
    }

    public void testGetInverseSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();

        ImmutableSet<X1CCManyId<?>> ccIds = ImmutableSet.of(x1CCManyMockupFactory.getC1Id(), x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);
        Map<X1CCManyId<?>, Set<X1AAId<?>>> invSomeCCsIdsMap = x1AAFinderService.findInvSomeCCsIds(ccIds);
        assertThat(invSomeCCsIdsMap).hasSize(3);
        assertThat(invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC1Id())).hasSize(0);
        assertThat(invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC2Id())).containsOnly(x1AAMockupFactory.getA3Id());
        assertThat(invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC3Id())).containsOnly(x1AAMockupFactory.getA3Id());
    }


    public void testChangeSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        X1CCMany c1 = store.get(x1CCManyMockupFactory.getC1Id());
        assertThat(c1.findInvSomeCCsIds()).isEmpty();

        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.getSomeCCsIds().add(c1.getId());
        assertThat(c1.findInvSomeCCsIds()).containsOnly(a2.getId());
    }


    public void testGetUnique() {

    }

    public void testManyToMany() {

    }
}
