package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
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
import static org.testng.Assert.assertNull;

/**
 * Tester kodepattern for unidireksjonelle relasjoner med tilhørede invers findere.
 *
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
                return ImmutableSet.copyOf(Iterables.concat(
                        mockupFacade.getX1AAMockupFactory().getAllIds(X1AAId.class),
                        mockupFacade.getX1BBOneMockupFactory().getAllIds(X1BBOneId.class),
                        mockupFacade.getX1CCManyMockupFactory().getAllIds(X1CCManyId.class)

                ));
            }
        });
    }

    /**
     * Tester mockups for relasjon "X1AA ---someBB-> X1BBOne"
     */
    public void testManyToOneMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        // Sjekk at a1 peker på b2
        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getSomeBBId(), x1BBOneMockupFactory.getB2Id());

        // Sjekk at a2 og a3 peker på b3
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        assertNotNull(a2);
        assertEquals(a2.getSomeBBId(), x1BBOneMockupFactory.getB3Id());
        X1AA a3 = store.get(x1AAMockupFactory.getA2Id());
        assertNotNull(a3);
        assertEquals(a3.getSomeBBId(), x1BBOneMockupFactory.getB3Id());
    }

    /**
     * Tester uthenting av invers relasjon "X1AA ---someBB-> X1BBOne"
     */
    public void testGetInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        // Sjekk b1 invers-peker på ingen a-er
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
        Set<X1AAId<?>> b1InvSomeBBIds = b1.findInvSomeBBIds();
        assertThat(b1InvSomeBBIds).isEmpty();

        // Sjekk b2 invers-peker på a1
        X1BBOne b2 = store.get(x1BBOneMockupFactory.getB2Id());
        Set<X1AAId<?>> b2InvSomeBBIds = b2.findInvSomeBBIds();
        assertThat(b2InvSomeBBIds).containsOnly(x1AAMockupFactory.getA1Id());

        // Sjekk b3 invers-peker på a2 og a3
        X1BBOne b3 = store.get(x1BBOneMockupFactory.getB3Id());
        Set<X1AAId<?>> b3InvSomeBBIds = b3.findInvSomeBBIds();
        assertThat(b3InvSomeBBIds).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }


    /**
     * Tester uthenting og caching av invers relasjon "X1AA ---someBB-> X1BBOne"
     */
    public void testGetCachedInverseManyToOneRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        ImmutableSet<X1BBOneId<?>> bbIds = ImmutableSet.of(x1BBOneMockupFactory.getB1Id(), x1BBOneMockupFactory.getB2Id(), x1BBOneMockupFactory.getB3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);

        // Relasjonscaching enables og invers relasjon for b1, b2 og b3 lastes inn
        store.getInstance(StoreRelationCache.class).setEnabled(true);
        store.get(bbIds);
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(bbIds);
        // Last inn alle X1AA og X1BBOne objekter
        store.get(ImmutableSet.copyOf(Iterables.concat(invSomeBBIds.values())));
        store.get(invSomeBBIds.keySet());

        // Her fra er invers relasjoner og tilhørende objekter lastet. Ikke flere serverkall.

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

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne".
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen materialisert før endring.
     */
    public void testChangeSomeBBRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

        // Sjekk at b1 ikke er invers relatert til a2.
        assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
        assertEquals(b1.getInvSomeBBIds().isMaterialised(), false);
        assertNull(b1.getInvSomeBBIds().getCached());

        // Her endres a2 til å peke på b1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.setSomeBBId(b1.getId());

        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne".
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen materialisert før endring i en unit-of-work.
     */
    public void testChangeSomeBBRelation4() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);

        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

            // Sjekk at b1 ikke er invers relatert til a2.
            assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
            assertEquals(b1.getInvSomeBBIds().isMaterialised(), false);
            assertNull(b1.getInvSomeBBIds().getCached());

            // Her endres a2 til å peke på b1
            X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
            a2.setSomeBBId(b1.getId());

            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
        } finally {
            unitOfWork.close();
        }
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen endret før den materialiseres, men uten unit-of-work
     */
    public void testChangeSomeBBRelation2() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

        // Ikke sjekk at b1 ikke er invers relatert til a2.
//        assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
        assertEquals(b1.getInvSomeBBIds().isMaterialised(), false);
        assertNull(b1.getInvSomeBBIds().getCached());

        // Her endres a2 til å peke på b1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.setSomeBBId(b1.getId());

        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen endret før den materialiseres, men i unit-of-work
     */
    public void testChangeSomeBBRelation3() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getInstance(StoreRelationCache.class).setEnabled(true);
        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

            // Ikke sjekk at b1 ikke er invers relatert til a2.
//            assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
            assertEquals(b1.getInvSomeBBIds().isMaterialised(), false);
            assertNull(b1.getInvSomeBBIds().getCached());

            // Her endres a2 til å peke på b1
            X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
            a2.setSomeBBId(b1.getId());

            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
        } finally {
            unitOfWork.close();
        }
    }

    /**
     * Tester mockups for relasjon "X1AA ---someCCs-> X1CCMany"
     */
    public void testGetManyMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();
        store.getInstance(StoreRelationCache.class).setEnabled(true);

        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertThat(a1.getSomeCCsIds()).hasSize(0);

        X1AA a3 = store.get(x1AAMockupFactory.getA3Id());
        assertThat(a3.getSomeCCsIds()).hasSize(2);
        assertThat(a3.getSomeCCsIds()).containsOnly(x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id());
    }

    /**
     * Tester uthenting  av invers relasjon "X1AA ---someCCs-> X1CCMany"
     */
    public void testGetInverseSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();
        store.getInstance(StoreRelationCache.class).setEnabled(true);

        ImmutableSet<X1CCManyId<?>> ccIds = ImmutableSet.of(x1CCManyMockupFactory.getC1Id(), x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);
        Map<X1CCManyId<?>, X1AAId<?>> invSomeCCsIdsMap = x1AAFinderService.findInvSomeCCsId(ccIds);
        assertThat(invSomeCCsIdsMap).hasSize(3);
        assertThat(invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC1Id())).isNull();
        assertThat((X1AAId)invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC2Id())).isEqualTo(x1AAMockupFactory.getA3Id());
        assertThat((X1AAId)invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC3Id())).isEqualTo(x1AAMockupFactory.getA3Id());
    }


    /**
     * Tester oppdatering av invers relasjon "X1AA ---someCCs-> X1CCMany"
     * Oppdatere relasjonen  melom a2 og c1 slik at disse blir relaterte.
     */
    public void testChangeSomeCCsRelation() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();

        // Sjekk at c1 ikke er invers relatert til a2.
        store.getInstance(StoreRelationCache.class).setEnabled(true);
        X1CCMany c1 = store.get(x1CCManyMockupFactory.getC1Id());
        assertThat(c1.findInvSomeCCsIds()).isNull();

        // Her endres a2 til å peke på c1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.getSomeCCsIds().add(c1.getId());
        assertThat((X1AAId)c1.findInvSomeCCsIds()).isEqualTo(a2.getId());
    }

    public void testNewObjects() {
        StoreRelationCache storeRelationCache = store.getInstance(StoreRelationCache.class);
        boolean enabled = storeRelationCache.isEnabled();
        storeRelationCache.setEnabled(true);
        UnitOfWork unitOfWork = store.beginUnitOfWork();
        try {
            X1AA a = new X1AA();
            store.insert(a);

            X1BBOne b = new X1BBOne();
            store.insert(b);

            a.setSomeBBId(b.getId());

            Set<X1AAId<?>> invSomeBBIds = b.findInvSomeBBIds();
            assertThat(invSomeBBIds).containsExactly(a.getId());
        } finally {
            unitOfWork.close();
            storeRelationCache.setEnabled(enabled);
        }
    }

    public void testNewObjectsDifferentLevels() {
        StoreRelationCache storeRelationCache = store.getInstance(StoreRelationCache.class);
        boolean enabled = storeRelationCache.isEnabled();
        storeRelationCache.setEnabled(true);
        UnitOfWork unitOfWork1 = store.beginUnitOfWork();
        try {
            X1AAId<?> aId;
            X1BBOneId<?> bId;
            UnitOfWork unitOfWork2 = store.beginUnitOfWork();
            try {
                X1AA a = new X1AA();
                store.insert(a);
                aId = a.getId();

                X1BBOne b = new X1BBOne();
                store.insert(b);
                bId = b.getId();

                a.setSomeBBId(b.getId());
                store.commitUnitOfWork(unitOfWork2);
            } finally {
                unitOfWork2.close();
            }

            X1AA a = store.get(aId);
            X1BBOne b = store.get(bId);

            Set<X1AAId<?>> invSomeBBIds = b.findInvSomeBBIds();
            assertThat(invSomeBBIds).containsExactly(a.getId());
        } finally {
            unitOfWork1.close();
            storeRelationCache.setEnabled(enabled);
        }
    }

    public void testGetUnique() {

    }

    public void testManyToMany() {

    }
}
