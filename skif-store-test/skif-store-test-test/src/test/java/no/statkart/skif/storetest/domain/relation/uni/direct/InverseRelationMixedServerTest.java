package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.*;
import no.statkart.skif.storetest.domain.relation.X1AAMockupFactory;
import no.statkart.skif.storetest.domain.relation.X1BBOneMockupFactory;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
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
    StoreUpdateService storeUpdateService;
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    /**
     * Alle testcaser bruke samme StoreClient instans. Dette sikre at cachet state i klient blir evicted på tvers av
     * tester
     */
    // TODO: Denne bør flyttes til SkifTestCase (tror jeg)
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
        store.cacheMaterialisedRelations(b);
        return store.get(b.getId());
    }

    /**
     * Hjelpestruktur som angir operasjoner som server skal gjøre på invers relasjon før boble sende til klient
     */
    private static enum Action {
        LOAD, REQUEST
    }

    ;

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
}
