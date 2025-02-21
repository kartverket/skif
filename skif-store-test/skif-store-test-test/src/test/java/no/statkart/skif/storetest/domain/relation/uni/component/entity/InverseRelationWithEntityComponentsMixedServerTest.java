package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleTransfer;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Tester InverseRelation materialisering fra server til klient. Bobler som inneholder materialiserte relasjoner
 * får deres relasjoner lagt inn i cachen når de registreres i klienten.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Test(groups = "singlevm-required")
public class InverseRelationWithEntityComponentsMixedServerTest extends StoreTestMixedTestCase {

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

    private X2BBOne register(X2BBOne b) {
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


    private X2BBOne getBBOneWithRelationMaterialized(final @Nullable X2BBOneId<?> bId, final Action... actions) {
        return (X2BBOne) server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                X2BBOne b = store.get(bId);
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
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id());
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

        X2BBOne b3 = register(getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB3Id()));
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }

    /**
     * Tester at materialiserte relasjoner ikke sendes til klient når requested ikke er satt
     */
    public void testSerializationMaterialisedUnrequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.LOAD);
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

        X2BBOne b3 = register(getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB3Id(), Action.LOAD));
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }


    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt
     */
    public void testSerializationUnmaterialisedRequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.REQUEST);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        // Registrer b1 i Store. Siden det ikke er gjort endringer blir resultatet det samme
        register(b1);
        assertFalse(b1.getInvSomeBBIds().isMaterialised(), "Forventet isMaterialised returnerer false etter registerering i Store på klient");
        assertFalse(b1.getInvSomeBBIds().isRequested(), "Forventet isRequested returnerer false etter registerering i Store på klient");
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X2BBOne b3 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB3Id(), Action.REQUEST);
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }

    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt. Operasjonsrekkefølge som
     * testes på server er LOAD først, så REQUEST . Tester også at materialiserte relasjoner blir
     * automatisk blir registrert i RelationCache på klienten ved store.register().
     */
    public void testSerializationMaterialisedRequestedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();
        store.getRelationCache().setEnabled(true);

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.LOAD, Action.REQUEST);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        assertFalse(store.getRelationCache().isMaterialised(b1.getInvSomeBBIds().getName(), b1.getId()));
        // Registrer b1 i Store.
        register(b1);
        assertTrue(store.getRelationCache().isMaterialised(b1.getInvSomeBBIds().getName(), b1.getId()));
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();

        X2BBOne b3 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB3Id(), Action.LOAD, Action.REQUEST);
        assertThat(b3.getInvSomeBBIds().get()).containsOnly(X2AAWithEntityComponentMockupFactory.getA2Id(), X2AAWithEntityComponentMockupFactory.getA3Id());
    }

    /**
     * Tester at umaterialiserte relasjoner sendes til klient når requested er satt. Operasjonsrekkefølge som
     * testes på server er REQUEST først, så LOAD . Tester også at materialiserte relasjoner
     * automatisk blir registrert i RelationCache på klienten ved store.register().
     */
    public void testSerializationRequestedMaterialisedMany() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();
        store.getRelationCache().setEnabled(true);

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.REQUEST, Action.LOAD);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());

        // Registrer b1 i Store.
        register(b1);
        assertTrue(store.getRelationCache().isMaterialised(b1.getInvSomeBBIds().getName(), X2BBOneMockupFactory.getB1Id()));
        assertThat(b1.getInvSomeBBIds().get()).isEmpty();
    }

    /**
     * Tester at materialiserte relasjoner som hentes via objekt legges inn i relasjonscachen når objektet
     * ikke er lastet fra før i klienten.
     */
    public void testRegistrerObjectMedMateralisertRelasjonNaarObjektIkkeErLastetIStore() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();
        store.getRelationCache().setEnabled(true);

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.REQUEST, Action.LOAD);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());

        // Registrer b1 i Store.
        register(b1);
        assertTrue(store.getRelationCache().isMaterialised(X2AAWithEntityComponentFinderService.Role.someBB, X2BBOneMockupFactory.getB1Id()));
    }

    /**
     * Tester at materialiserte relasjoner som hentes via objekt legges inn i relasjonscachen når objektet
     * er lastet fra før av klienten, men ikke låst.
     */
    public void testRegistrerObjectMedMateralisertRelasjonObjektAlleredeErLastetIStore() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();
        store.getRelationCache().setEnabled(true);

        X2BBOne eksisterende = store.get(X2BBOneMockupFactory.getB1Id());
        assertFalse(store.getRelationCache().isMaterialised(X2AAWithEntityComponentFinderService.Role.someBB, X2BBOneMockupFactory.getB1Id()));

        X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.REQUEST, Action.LOAD);
        assertNotSame(eksisterende, b1);
        assertTrue(b1.getInvSomeBBIds().isMaterialised());
        assertTrue(b1.getInvSomeBBIds().isRequested());

        // Registrer b1 i Store.
        register(b1);
        assertTrue(store.getRelationCache().isMaterialised(X2AAWithEntityComponentFinderService.Role.someBB, X2BBOneMockupFactory.getB1Id()));
    }


    /**
     * Tester at objekt med materialiserte relasjoner som hentes via utenom om Store og deretter registreres ikke
     * legges inn i Store når det allrede finnes en annen instans som er låst. Relasjonscachen oppdateres heller ikke.
     */
    public void testRegistrerObjectMedMateralisertRelasjonNaarLockedVersonPaaKlient() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X2AAWithEntityComponentMockupFactory X2AAWithEntityComponentMockupFactory = mockupFacade.getX2AAWithEntityComponentMockupFactory();
        final X2BBOneMockupFactory X2BBOneMockupFactory = mockupFacade.getX2BBOneMockupFactory();

        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            store.getRelationCache().setEnabled(true);

            X2BBOne eksisterendeSomErLocked = store.lock(X2BBOneMockupFactory.getB1Id());
            assertFalse(store.getRelationCache().isMaterialised(X2AAWithEntityComponentFinderService.Role.someBB, X2BBOneMockupFactory.getB1Id()));

            X2BBOne b1 = getBBOneWithRelationMaterialized(X2BBOneMockupFactory.getB1Id(), Action.REQUEST, Action.LOAD);
            assertNotSame(eksisterendeSomErLocked, b1);
            assertTrue(b1.getInvSomeBBIds().isMaterialised());
            assertTrue(b1.getInvSomeBBIds().isRequested());

            // Registrer b1 i Store.
            register(b1);
            assertSame(store.get(X2BBOneMockupFactory.getB1Id()), eksisterendeSomErLocked);
            assertFalse(store.getRelationCache().isMaterialised(X2AAWithEntityComponentFinderService.Role.someBB, X2BBOneMockupFactory.getB1Id()));
        }
    }
}
