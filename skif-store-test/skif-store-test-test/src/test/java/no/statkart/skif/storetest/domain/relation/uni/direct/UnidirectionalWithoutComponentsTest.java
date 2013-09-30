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
                        mockupFacade.getX1AAMockupFactory().getAllIds(X1AAId.class),
                        mockupFacade.getX1BBOneMockupFactory().getAllIds(X1BBOneId.class)
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

        ImmutableSet<X1BBOneId<?>>bbIds = ImmutableSet.of(x1BBOneMockupFactory.getB1Id(), x1BBOneMockupFactory.getB2Id(), x1BBOneMockupFactory.getB3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        store.get(bbIds);
        Map<X1BBOneId<?>,Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(bbIds);


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

    public void testChangeRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1AA x1AA = store.get(x1AAMockupFactory.getA1Id());
        x1AA.setSomeBBId(x1BBOneMockupFactory.getB2Id());

    }
    public void testGetMany() {

    }

    public void testGetUnique() {

    }

    public void testManyToMany() {

    }
}
