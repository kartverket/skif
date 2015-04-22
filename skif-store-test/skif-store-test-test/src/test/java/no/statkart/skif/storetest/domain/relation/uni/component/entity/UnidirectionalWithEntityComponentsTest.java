package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
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
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class UnidirectionalWithEntityComponentsTest extends StoreTestTestCase {
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
                                mockupFacade.getX2AAWithEntityComponentMockupFactory().getAllIds(X2AAWithEntityComponentId.class),
                                mockupFacade.getX2BBOneMockupFactory().getAllIds(X2BBOneId.class)
                        ),
                        mockupFacade.getX2CCManyMockupFactory().getAllIds(X2CCManyId.class)
                );
            }
        });
    }

    public void testManyToOneMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        X2AAWithEntityComponent a1 = store.get(X2AAWithEntityComponentMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getEntityComponentOne().getSomeBBId(), X2BBOneMockupFactory.getB2Id());

        // Sjekk at a2 og a3 peker på samme B
        X2AAWithEntityComponent a2 = store.get(X2AAWithEntityComponentMockupFactory.getA2Id());
        assertNotNull(a2);
        assertEquals(a2.getEntityComponentOne().getSomeBBId(), X2BBOneMockupFactory.getB3Id());

        X2AAWithEntityComponent a3 = store.get(X2AAWithEntityComponentMockupFactory.getA2Id());
        assertNotNull(a3);
        assertEquals(a3.getEntityComponentOne().getSomeBBId(), X2BBOneMockupFactory.getB3Id());
    }

    public void testGetInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        X2BBOne b1 = store.get(X2BBOneMockupFactory.getB1Id());
        Set<X2AAWithEntityComponentId<?>> b1InvSomeBBIds = b1.findInvSomeBBIds();
        assertThat(b1InvSomeBBIds).isEmpty();

        X2BBOne b2 = store.get(X2BBOneMockupFactory.getB2Id());
        Set<X2AAWithEntityComponentId<?>> b2InvSomeBBIds = b2.findInvSomeBBIds();
        assertThat(b2InvSomeBBIds).containsOnly(X2AAWithEntityComponentMockupFactory.getA1Id());

        X2BBOne b3 = store.get(X2BBOneMockupFactory.getB3Id());
        Set<X2AAWithEntityComponentId<?>> b3InvSomeBBIds = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }


    public void testGetCachedInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        ImmutableSet<X2BBOneId<?>> bbIds = ImmutableSet.of(X2BBOneMockupFactory.getB1Id(), X2BBOneMockupFactory.getB2Id(), X2BBOneMockupFactory.getB3Id());
        X2AAWithEntityComponentFinderService X2AAWithEntityComponentFinderService = store.getInstance(X2AAWithEntityComponentFinderService.class);

        store.getRelationCache().setEnabled(true);
        store.get(bbIds);
        Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> invSomeBBIds = X2AAWithEntityComponentFinderService.findInvSomeBBIds(bbIds);


        X2BBOne b1 = store.get(X2BBOneMockupFactory.getB1Id());
        Set<X2AAWithEntityComponentId<?>> b1InvSomeBBIds = b1.findInvSomeBBIds();
        assertThat(b1InvSomeBBIds).isEmpty();

        X2BBOne b2 = store.get(X2BBOneMockupFactory.getB2Id());
        Set<X2AAWithEntityComponentId<?>> b2InvSomeBBIds = b2.findInvSomeBBIds();
        assertThat(b2InvSomeBBIds).containsOnly(X2AAWithEntityComponentMockupFactory.getA1Id());

        X2BBOne b3 = store.get(X2BBOneMockupFactory.getB3Id());
        Set<X2AAWithEntityComponentId<?>> b3InvSomeBBIds = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());

        Set<X2AAWithEntityComponentId<?>> b3InvSomeBBIds2 = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds2).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }

    public void testChangeSomeBBRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);
        X2BBOne b1 = store.get(X2BBOneMockupFactory.getB1Id());
        assertThat(b1.findInvSomeBBIds()).doesNotContain(X2AAWithEntityComponentMockupFactory.getA2Id());

        X2AAWithEntityComponent a2 = store.get(X2AAWithEntityComponentMockupFactory.getA2Id());
        a2.getEntityComponentOne().setSomeBBId(b1.getId());
        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
    }

    public void testChangeSomeBBRelationWithDisconnectedComponent() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        UnitOfWork unitOfWork = null;
        try {
            unitOfWork = store.beginUnitOfWork();
            store.getRelationCache().setEnabled(true);
            X2BBOne b1 = store.get(X2BBOneMockupFactory.getB1Id());
            assertThat(b1.findInvSomeBBIds()).doesNotContain(X2AAWithEntityComponentMockupFactory.getA2Id());
            X2AAWithEntityComponent a2 = store.get(X2AAWithEntityComponentMockupFactory.getA2Id());
            X2EntityComponentOne aNewComponent = new X2EntityComponentOne();
            aNewComponent.setSomeBBId(b1.getId());
            a2.setEntityComponentOne(aNewComponent);
            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
        } finally {
            unitOfWork.close();
        }
    }

    public void testGetMany() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2CCManyMockupFactory X2CCManyMockupFactory = mockupFacade.getX2CCManyMockupFactory();
        store.getRelationCache().setEnabled(true);

        X2AAWithEntityComponent a1 = store.get(X2AAWithEntityComponentMockupFactory.getA1Id());
        assertThat(a1.getEntityComponentOne().getSomeCCsIds()).hasSize(0);

        X2AAWithEntityComponent a3 = store.get(X2AAWithEntityComponentMockupFactory.getA3Id());
        assertThat(a3.getEntityComponentOne().getSomeCCsIds()).hasSize(2);
        assertThat(a3.getEntityComponentOne().getSomeCCsIds()).containsOnly(X2CCManyMockupFactory.getC2Id(), X2CCManyMockupFactory.getC3Id());
    }

    public void testGetInverseSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2CCManyMockupFactory X2CCManyMockupFactory = mockupFacade.getX2CCManyMockupFactory();
        store.getRelationCache().setEnabled(true);

        ImmutableSet<X2CCManyId<?>> ccIds = ImmutableSet.of(X2CCManyMockupFactory.getC1Id(), X2CCManyMockupFactory.getC2Id(), X2CCManyMockupFactory.getC3Id());
        X2AAWithEntityComponentFinderService X2AAWithEntityComponentFinderService = store.getInstance(X2AAWithEntityComponentFinderService.class);
        Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> invSomeCCsIdsMap = X2AAWithEntityComponentFinderService.findInvSomeCCsId(ccIds);
        assertThat(invSomeCCsIdsMap).hasSize(3);
        assertThat(invSomeCCsIdsMap.get(X2CCManyMockupFactory.getC1Id())).isNull();
        assertThat((X2AAWithEntityComponentId) invSomeCCsIdsMap.get(X2CCManyMockupFactory.getC2Id())).isEqualTo(X2AAWithEntityComponentMockupFactory.getA3Id());
        assertThat((X2AAWithEntityComponentId) invSomeCCsIdsMap.get(X2CCManyMockupFactory.getC3Id())).isEqualTo(X2AAWithEntityComponentMockupFactory.getA3Id());
    }

    public void testChangeSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        X2CCManyMockupFactory X2CCManyMockupFactory = mockupFacade.getX2CCManyMockupFactory();

        store.getRelationCache().setEnabled(true);
        X2CCMany c1 = store.get(X2CCManyMockupFactory.getC1Id());
        assertThat(c1.findInvSomeCCsIds()).isNull();

        X2AAWithEntityComponent a2 = store.get(X2AAWithEntityComponentMockupFactory.getA2Id());
        a2.getEntityComponentOne().getSomeCCsIds().add(c1.getId());
        assertThat((X2AAWithEntityComponentId) c1.findInvSomeCCsIds()).isEqualTo(a2.getId());
    }


    public void testGetUnique() {

    }

    public void testManyToMany() {

    }
}
