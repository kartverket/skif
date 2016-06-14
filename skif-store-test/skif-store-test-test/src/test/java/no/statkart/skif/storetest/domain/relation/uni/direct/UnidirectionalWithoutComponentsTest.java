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
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
        store.getRelationCache().setEnabled(true);
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
    public void testChangeSomeBBRelationCaseEndresEtterMaterialisering() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

        // Sjekk at b1 ikke er invers relatert til a2.
        assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id()); // her lastes relasjonen
        assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");

        // Her endres a2 til å peke på b1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.setSomeBBId(b1.getId());

        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen endret før den materialiseres, men uten unit-of-work
     */
    public void testChangeSomeBBRelationCaseEndresFoerMaterialiseringUtenUOW() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);
        X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
        assertFalse(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal ikke være lastet");

        // Her endres a2 til å peke på b1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.setSomeBBId(b1.getId());

        assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
        assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen endret før den materialiseres, men i unit-of-work
     */
    public void testChangeSomeBBRelationCaseEndresFoerMaterialiseringIUOW() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);
        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());
            assertFalse(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal ikke være lastet");

            // Her endres a2 til å peke på b1
            X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
            a2.setSomeBBId(b1.getId());

            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
            assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");
        }
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne".
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen materialisert før endring i en unit-of-work.
     */
    public void testChangeSomeBBRelationCaseEndresEtterMaterialiseringIUOW() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);

        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

            // Sjekk at b1 ikke er invers relatert til a2.
            assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
            assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");

            // Her endres a2 til å peke på b1
            X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
            a2.setSomeBBId(b1.getId());

            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
        }
    }

    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen materialisert før den endres og sjekkes på nytt i en nøstet unit-of-work.
     */
    public void testChangeSomeBBRelationCaseEndresEtterMaterialiseringINoestedUOW() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);
        UnitOfWork unitOfWork1 = store.beginUnitOfWork();
        try {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

            // Sjekk at b1 ikke er invers relatert til a2.
            assertThat(b1.findInvSomeBBIds()).doesNotContain(x1AAMockupFactory.getA2Id());
            assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");

            UnitOfWork unitOfWork2 = store.beginUnitOfWork();
            try {
                // Her endres a2 til å peke på b1
                X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
                a2.setSomeBBId(b1.getId());

                assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
            } finally {
                store.abortUnitOfWork(unitOfWork2);
            }
        } finally {
            store.abortUnitOfWork(unitOfWork1);
        }
    }


    /**
     * Tester oppdatering av invers relasjon "X1AA ---someBB-> X1BBOne"
     * Oppdatere relasjonen  melom a2 og b1 slik at disse blir relaterte.
     * I denne varianten blir relasjonen endret før den materialiseres, men i unit-of-work
     */
    public void testChangeSomeBBRelationDetached() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        store.getRelationCache().setEnabled(true);

        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1BBOne b1 = store.get(x1BBOneMockupFactory.getB1Id());

            // Her endres a2 til å peke på b1. Bemerk at a2 er detached så invers relasjon kan først oppdateres ved store.update(a2)
            X1AA a2 = CopyHelper.copy(store.lock(x1AAMockupFactory.getA2Id()));
            a2.setSomeBBId(b1.getId());
            assertFalse(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal ikke være lastet");
            store.update(a2);
            assertThat(b1.findInvSomeBBIds()).contains(a2.getId());
            assertTrue(store.getRelationCache().isMaterialized(b1.getInvSomeBBIds().getName(), b1.getId()), "Relasjon skal være lastet");
        }
    }


    /**
     * Tester mockups for relasjon "X1AA ---someCCs-> X1CCMany"
     */
    public void testGetManyMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1CCManyMockupFactory x1CCManyMockupFactory = mockupFacade.getX1CCManyMockupFactory();
        store.getRelationCache().setEnabled(true);

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
        store.getRelationCache().setEnabled(true);

        ImmutableSet<X1CCManyId<?>> ccIds = ImmutableSet.of(x1CCManyMockupFactory.getC1Id(), x1CCManyMockupFactory.getC2Id(), x1CCManyMockupFactory.getC3Id());
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);
        Map<X1CCManyId<?>, X1AAId<?>> invSomeCCsIdsMap = x1AAFinderService.findInvSomeCCsId(ccIds);
        assertThat(invSomeCCsIdsMap).hasSize(3);
        assertThat(invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC1Id())).isNull();
        assertThat((X1AAId) invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC2Id())).isEqualTo(x1AAMockupFactory.getA3Id());
        assertThat((X1AAId) invSomeCCsIdsMap.get(x1CCManyMockupFactory.getC3Id())).isEqualTo(x1AAMockupFactory.getA3Id());
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
        store.getRelationCache().setEnabled(true);
        X1CCMany c1 = store.get(x1CCManyMockupFactory.getC1Id());
        assertThat(c1.findInvSomeCCsIds()).isNull();

        // Her endres a2 til å peke på c1
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        a2.getSomeCCsIds().add(c1.getId());
        assertThat((X1AAId) c1.findInvSomeCCsIds()).isEqualTo(a2.getId());
    }

    public void testNewObjects() {
        StoreRelationCache storeRelationCache = store.getRelationCache();
        boolean enabled = storeRelationCache.isEnabled();
        storeRelationCache.setEnabled(true);

        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1AA a = new X1AA();
            store.insert(a);

            X1BBOne b = new X1BBOne();
            store.insert(b);

            a.setSomeBBId(b.getId());

            Set<X1AAId<?>> invSomeBBIds = b.findInvSomeBBIds();
            assertThat(invSomeBBIds).containsExactly(a.getId());
        } finally {
            storeRelationCache.setEnabled(enabled);
        }
    }

    public void testNewObjectsDifferentLevels() {
        StoreRelationCache storeRelationCache = store.getRelationCache();
        boolean enabled = storeRelationCache.isEnabled();
        storeRelationCache.setEnabled(true);

        //noinspection unused
        try (UnitOfWork unitOfWork1 = store.beginUnitOfWork()) {
            X1AAId<?> aId;
            X1BBOneId<?> bId;
            try (UnitOfWork unitOfWork2 = store.beginUnitOfWork()) {
                X1AA a = new X1AA();
                store.insert(a);
                aId = a.getId();

                X1BBOne b = new X1BBOne();
                store.insert(b);
                bId = b.getId();

                a.setSomeBBId(b.getId());
                store.commitUnitOfWork(unitOfWork2);
            }

            X1AA a = store.get(aId);
            X1BBOne b = store.get(bId);

            Set<X1AAId<?>> invSomeBBIds = b.findInvSomeBBIds();
            assertThat(invSomeBBIds).containsExactly(a.getId());
        } finally {
            storeRelationCache.setEnabled(enabled);
        }
    }

    /**
     * Tester mockups for "X1AA.uniqueOnX1AA"
     */
    public void testUniqueOnX1AAMockups() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();

        // Sjekk at a1 har gitt index verdier
        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getUniqueOnX1AA(), "Unique: [0,1]");
        assertEquals(a1.getNonUniqueOnX1AA(), "NonUnique: [0,0]");

        // Sjekk at a2 har  gitt index verdier
        X1AA a2 = store.get(x1AAMockupFactory.getA2Id());
        assertNotNull(a2);
        assertEquals(a2.getUniqueOnX1AA(), "Unique: [0,2]");
        assertEquals(a2.getNonUniqueOnX1AA(), "NonUnique: [0,1]");

        // Sjekk at a3 har gitt index verdier
        X1AA a3 = store.get(x1AAMockupFactory.getA3Id());
        assertNotNull(a3);
        assertEquals(a3.getUniqueOnX1AA(), "Unique: [0,3]");
        assertEquals(a3.getNonUniqueOnX1AA(), "NonUnique: [0,1]");
    }

    /**
     * Tester uthenting av "X1AA.uniqueOnX1AA"
     */
    public void testGetviaUniqueOnX1AAIndex() {
        store.getRelationCache().setEnabled(true);
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        ImmutableSet<String> indexes = ImmutableSet.of("Unique: [0,1]", "Unique: [0,2]", "Unique: [0,3]");
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);
        Map<String, X1AAId<?>> map = x1AAFinderService.findX1AAIdsForUniqueOnX1AA(indexes);

        assertEquals(map.size(), 3);
        assertNotNull(map.get("Unique: [0,1]"));
        assertNotNull(map.get("Unique: [0,2]"));
        assertNotNull(map.get("Unique: [0,3]"));
    }

    /**
     * Tester uthenting av "X1AA.uniqueOnX1AA" ved insert
     */
    public void testInsertUniqueOnX1AAIndex() {
        store.getRelationCache().setEnabled(true);
        mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);

        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1AA x1AA = new X1AA();
            x1AA.setUniqueOnX1AA("blabla");
            store.insert(x1AA);
            Map<String, X1AAId<?>> map = x1AAFinderService.findX1AAIdsForUniqueOnX1AA(ImmutableSet.of("blabla", "Unique: [0,2]"));
            assertNotNull(map.get("blabla"));
            assertEquals(map.get("blabla"), x1AA.getId());
            assertNotNull(map.get("Unique: [0,2]"));
        }
    }

    /**
     * Tester uthenting av "X1AA.NonUniqueOnX1AA"
     */
    public void testGetviaNonUniqueOnX1AAIndex() {
        store.getRelationCache().setEnabled(true);

        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();

        ImmutableSet<String> indexes = ImmutableSet.of("NonUnique: [0,0]", "NonUnique: [0,1]");
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);
        Map<String, Set<X1AAId<?>>> map = x1AAFinderService.findX1AAIdsForNonUniqueOnX1AA(indexes);

        assertEquals(map.size(), 2);
        assertNotNull(map.get("NonUnique: [0,0]"));
        assertEquals(map.get("NonUnique: [0,0]"), ImmutableSet.of(x1AAMockupFactory.getA1Id()));
        assertNotNull(map.get("NonUnique: [0,1]"));
        assertEquals(map.get("NonUnique: [0,1]"), ImmutableSet.of(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id()));
    }

    /**
     * Tester uthenting av "X1AA.uniqueOnX1AA" ved insert
     */
    public void testInsertNonUniqueOnX1AAIndex() {
        store.getRelationCache().setEnabled(true);
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1AAFinderService x1AAFinderService = store.getInstance(X1AAFinderService.class);

        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1AA x1AA = new X1AA();
            x1AA.setUniqueOnX1AA("blabla");
            x1AA.setNonUniqueOnX1AA("NonUnique: [0,1]");
            store.insert(x1AA);
            Map<String, Set<X1AAId<?>>> map = x1AAFinderService.findX1AAIdsForNonUniqueOnX1AA(ImmutableSet.of("NonUnique: [0,1]"));
            assertNotNull(map.get("NonUnique: [0,1]"));
            assertEquals(map.get("NonUnique: [0,1]"), ImmutableSet.of(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id(), x1AA.getId()));

            x1AA.setNonUniqueOnX1AA(null);
            Map<String, Set<X1AAId<?>>> map2 = x1AAFinderService.findX1AAIdsForNonUniqueOnX1AA(ImmutableSet.of("NonUnique: [0,1]"));
            assertNotNull(map2.get("NonUnique: [0,1]"));
            assertEquals(map2.get("NonUnique: [0,1]"), ImmutableSet.of(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id()));
        }
    }

    public void testEnableCacheAfterInsert() {
        try (UnitOfWork ignored = store.beginUnitOfWork()) {
            X1AA x1AA = new X1AA();
            X1BBOne x1BBOne = new X1BBOne();
            store.insert(x1BBOne);
            x1AA.setSomeBBId(x1BBOne.getId());
            store.insert(x1AA);
            store.getRelationCache().setEnabled(true);
            assertThat(x1BBOne.findInvSomeBBIds()).containsExactly(x1AA.getId());
        }
    }

    public void testEnableCacheAfterUpdate() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        // Sjekk at a1 peker på b2
        X1AA a1 = store.get(x1AAMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getSomeBBId(), x1BBOneMockupFactory.getB2Id());

        //noinspection unused
        try (UnitOfWork unitOfWork = store.beginUnitOfWork()) {
            X1BBOne x1BBOne = new X1BBOne();
            store.insert(x1BBOne);
            X1AA a1Changed = store.lock((x1AAMockupFactory.getA1Id()));
            a1Changed.setSomeBBId(x1BBOne.getId());
            store.update(a1Changed);
            //noinspection unused
            try (UnitOfWork unitOfWork2 = store.beginUnitOfWork()) {
                store.getRelationCache().setEnabled(true);
                assertThat(x1BBOne.findInvSomeBBIds()).containsExactly(a1Changed.getId());
            }
        }
    }
}