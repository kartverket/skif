package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleTransfer;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.relation.X1AAMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1BBOneMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test av InverseRelation properties for domeneobjekter.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = "singlevm-required")
public class InverseRelationMixedServerTest extends StoreTestMixedTestCase {

    @Inject
    StoreClient store;

    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    /**
     * Alle testcaser bruke samme StoreClient instans. Dette sikre at cachet state i klient blir evicted på tvers av
     * tester
     */
    @BeforeMethod
    protected void evictAll() {
        store.evictAll();
    }

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

    private X1BBOne register(X1BBOne b) {
        store.register(new BubbleTransfer<Void>(null, Collections.singletonList(b)) {
        });
        return store.get(b.getId());
    }

    /**
     * Hjelpestruktur som angir operasjoner som server skal gjøre på invers relasjon før boble sende til klient
     */
    private enum Action {
        LOAD, REQUEST
    }

    private X1BBOne getBBOne(final @Nullable X1BBOneId<?> bId, final Action... actions) {
        return (X1BBOne) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                X1BBOne b = store.get(bId);
                assertFalse(b.getInvSomeBBIds().isMaterialised());
                assertFalse(b.getInvSomeBBIds().isRequested());

                for (Action action : actions) {
                    switch (action) {
                        case LOAD:
                            b.getInvSomeBBIds().get();
                            break;
                        case REQUEST:
                            b.getInvSomeBBIds().setRequested();
                            break;
                    }
                }
                store.materialiseRequestedRelations(b);
                return b;
            }
        });
    }

    public void testSerializationUnmaterialisedUnrequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = getBBOne(x1BBOneMockupFactory.getB1Id());
        assertFalse(b1.getInvSomeBBIds().isMaterialised());
        assertFalse(b1.getInvSomeBBIds().isRequested());

        try {
            b1.getInvSomeBBIds().get();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            // Relasjonen var ikke cachet og kopiobjekt har ikke store så det går ikke an å hente relasjon
        }

        // Registrer objekt i store slik at relasjon kan hentes
        register(b1);
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X1BBOne b3 = register(getBBOne(x1BBOneMockupFactory.getB3Id()));
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }

    /**
     * Tester at materialiserte relasjoner ikke sendes til klient når requested ikke er satt
     */
    public void testSerializationMaterialisedUnrequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = getBBOne(x1BBOneMockupFactory.getB1Id(), Action.LOAD);
        assertFalse(b1.getInvSomeBBIds().isMaterialised());
        assertFalse(b1.getInvSomeBBIds().isRequested());

        try {
            b1.getInvSomeBBIds().get();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            // Relasjonen var ikke cachet og kopiobjekt har ikke store så det går ikke an å hente relasjon
        }

        // Registrer objekt i store slik at relasjon kan materialiseres
        register(b1);
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X1BBOne b3 = register(getBBOne(x1BBOneMockupFactory.getB3Id(), Action.LOAD));
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }


    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt
     */
    public void testSerializationUnmaterialisedRequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = getBBOne(x1BBOneMockupFactory.getB1Id(), Action.REQUEST);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        // Registrer b1 i Store. Siden det ikke er gjort endringer blir resultatet det samme
        register(b1);
        assertFalse(b1.getInvSomeBBIds().isMaterialised(), "Forventet isMaterialised returnerer false etter registerering i Store på klient");
        assertFalse(b1.getInvSomeBBIds().isRequested(), "Forventet isRequested returnerer false etter registerering i Store på klient");
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X1BBOne b3 = getBBOne(x1BBOneMockupFactory.getB3Id(), Action.REQUEST);
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }

    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt
     */
    public void testSerializationMaterialisedRequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = getBBOne(x1BBOneMockupFactory.getB1Id(), Action.LOAD, Action.REQUEST);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        // Registrer b1 i Store. Siden det ikke er gjort endringer blir resultatet det samme
        register(b1);
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X1BBOne b3 = getBBOne(x1BBOneMockupFactory.getB3Id(), Action.LOAD, Action.REQUEST);
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }

    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt
     */
    public void testSerializationRequestedMaterialisedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();

        X1BBOne b1 = getBBOne(x1BBOneMockupFactory.getB1Id(), Action.REQUEST, Action.LOAD);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());

        // Registrer b1 i Store. Siden det ikke er gjort endringer blir resultatet det samme
        register(b1);
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X1BBOne b3 = getBBOne(x1BBOneMockupFactory.getB3Id(), Action.REQUEST, Action.LOAD);
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(x1AAMockupFactory.getA2Id(), x1AAMockupFactory.getA3Id());
    }

    /**
     * Tester at beregning av invers relasjoner blir korrekt når relation caching er disabled og invers relasjonen
     * endres på serveren utenom klienten
     */
    public void testUpdateRelationsOnServerWithRelationCachingOnClientDisabled() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();
        ImmutableSet<X1BBOneId<?>> x1BBOneIds = ImmutableSet.of(x1BBOneMockupFactory.getB1Id(), x1BBOneMockupFactory.getB1Id());
        store.get(x1BBOneIds);
        assertThat(store.getRelationCache().isEnabled()).isEqualTo(false);
        assertThat(store.get(x1BBOneMockupFactory.getB1Id()).findInvSomeBBIds()).isEmpty();
        assertThat(store.get(x1BBOneMockupFactory.getB2Id()).findInvSomeBBIds()).containsOnly(x1AAMockupFactory.getA1Id());
        updateAAOnServer(x1AAMockupFactory.getA1Id(), x1BBOneMockupFactory.getB1Id());
        assertThat(store.get(x1BBOneMockupFactory.getB1Id()).findInvSomeBBIds()).containsOnly(x1AAMockupFactory.getA1Id());
        assertThat(store.get(x1BBOneMockupFactory.getB2Id()).findInvSomeBBIds()).isEmpty();
    }

    /**
     * Tester at beregning av invers relasjoner blir korrekt når relation caching er enabled og inversrelasjonen
     * endres på serveren utenom klienten - dersom man kaller Store.evictAll() før invers relasjonen beregnes.
     */
    public void testUpdateRelationsOnServerWithCachingOnClientEnabledEvictAll() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();
        ImmutableSet<X1BBOneId<?>> x1BBOneIds = ImmutableSet.of(x1BBOneMockupFactory.getB1Id(), x1BBOneMockupFactory.getB1Id());
        store.get(x1BBOneIds);
        try {
            store.getRelationCache().setEnabled(true);
            assertThat(store.getRelationCache().isEnabled()).isEqualTo(true);
            assertThat(store.get(x1BBOneMockupFactory.getB1Id()).findInvSomeBBIds()).isEmpty();
            assertThat(store.get(x1BBOneMockupFactory.getB2Id()).findInvSomeBBIds()).containsOnly(x1AAMockupFactory.getA1Id());
            updateAAOnServer(x1AAMockupFactory.getA1Id(), x1BBOneMockupFactory.getB1Id());
            store.evictAll(); // Uten denne feiler koden fordi relasjoner som er endret på serveren er cachet på klienten
            assertThat(store.get(x1BBOneMockupFactory.getB1Id()).findInvSomeBBIds()).containsOnly(x1AAMockupFactory.getA1Id());
            assertThat(store.get(x1BBOneMockupFactory.getB2Id()).findInvSomeBBIds()).isEmpty();
        } finally {
            store.getRelationCache().setEnabled(false);
            assertThat(store.getRelationCache().isEnabled()).isEqualTo(false);
        }
    }

    /**
     * Tester at beregning av invers relasjoner blir korrekt når relation caching er enabled og inversrelasjonen
     * endres på serveren utenom klienten - dersom man kaller Store.evict() for boblen som er endret. Cachet
     * inversrelasjoner som blir berørt skal da bli riktige likevel. Case a1->b2 endres til a1->b1. Se SKIF-583.
     */
    public void testUpdateRelationsOnServerWithCachingOnClientEnabledEvictBubble() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final X1AAMockupFactory x1AAMockupFactory = mockupFacade.getX1AAMockupFactory();
        final X1BBOneMockupFactory x1BBOneMockupFactory = mockupFacade.getX1BBOneMockupFactory();
        X1AAId<?> a1Id = x1AAMockupFactory.getA1Id();
        X1BBOneId<?> b1Id = x1BBOneMockupFactory.getB1Id();
        X1BBOneId<?> b2Id = x1BBOneMockupFactory.getB2Id();
        ImmutableSet<? extends BubbleId<? extends BubbleObject>> ids = ImmutableSet.of(b1Id, b2Id, a1Id);
        try {
            store.getRelationCache().setEnabled(true);
            store.get(ImmutableSet.of(b1Id, b2Id));

            assertThat(store.getRelationCache().isEnabled()).isEqualTo(true);
            assertThat(store.get(b1Id).findInvSomeBBIds()).isEmpty();
            assertThat(store.get(b2Id).findInvSomeBBIds()).containsExactly(a1Id);
            updateAAOnServer(a1Id, b1Id); // Her oppdateres a1 på serveren (uten om klienten) til å peke på b1 istedet for b2
            X1AA a1 = store.get(a1Id);
            assertEquals(a1.getSomeBBId(), b1Id); // a1 peker nå på b1
            // Materialisert relasjoner for a1 er feil fordi de fortsatt er cachet. Skal ikke være tom.
            assertThat(store.getRelationCache().isMaterialised(store.get(b1Id).getInvSomeBBIds().getName(), b1Id));
            assertThat(store.get(b1Id).findInvSomeBBIds()).isEmpty();
            assertThat(store.get(b2Id).findInvSomeBBIds()).containsExactly(a1Id);

            // Dette tømmer relasjonscachen. Hadde vært fint om det ikke var nødvendig.
            store.getRelationCache().setEnabled(false);
            store.getRelationCache().setEnabled(true);

            // Da blir svarene riktig
            assertEquals(store.get(a1Id).getSomeBBId(), b1Id);
            assertThat(store.getRelationCache().isEnabled()).isEqualTo(true);
            assertThat(store.get(b1Id).findInvSomeBBIds()).containsExactly(a1Id);;
            assertThat(store.get(b2Id).findInvSomeBBIds()).isEmpty();
        } finally {
            store.getRelationCache().setEnabled(false);
            assertThat(store.getRelationCache().isEnabled()).isEqualTo(false);
        }
    }

    /**
     * Hjelpemetode som oppdatere relasjon fra X1AA til X1BBOne på serveren uten om klienten. Metoden kjører
     * i en egen transaksjon.
     */
    private void updateAAOnServer(final X1AAId<?> aId, final X1BBOneId<?> bbOneId) {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                X1AA a = store.lock(aId);
                a.setSomeBBId(bbOneId);
                store.update(a);
                return null;
            }
        });
    }

}
