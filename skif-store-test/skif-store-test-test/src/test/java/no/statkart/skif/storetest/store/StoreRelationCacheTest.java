package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAIdent;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCManyId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * TODO: Dette bør nok stå et annet sted som dokumentasjon også. Det står her for å gi er overblikk av hva som bør testes her
 * <p/>
 * Tester relationcaching gjennom Store, herunder samspillet med relation cachingen og Unit of Work konseptet. Store
 * støtter at relation caching kan slås av og på, dog pt med den begrensningen at caching ikke kan slås av hvis
 * den er slått på for underliggende Unit of Work.
 * <p/>
 * Et sentralt konsept i Unit of Work konseptet er at de endringer som gjøres innefor en unit of work begrenses til denne
 * i en vis forstand. Med hensyn til transaksjoner er det at endringer committes til underliggende session ved commit og
 * at endringene rulles tilbake (dvs ikke påvirker underliggende lag) ved abort. For caching betyder det at cachet
 * relasjoner må oppdateres riktig slik at endringer blir med ved commit og ikke med ved abort. Med hensyn til enabling
 * og disabling av relation caching skal en Unit of Work når den avsluttes ikke endre på underliggendes sessions setting.
 * Dvs hvis underliggende session har caching slått på skal den forbli slått på og hvis underliggende session har den
 * slått av skal caching automatisk slås av når Unit of Work avsluttes, hvis den ble slått på i Unit of Work. Når en
 * Unit of Work startes skal den arve den underliggende sessions cache setting. Endelig, når relation caching slås på må
 * den ta hensyn til at Store allerede kan inneholde endret objekter som caching må ta hensyn til for at resultatet
 * for relasjonen skal bli riktig. Her er det viktig å forstå at cachingen må se på alle låste objekter som er lastet.
 * Dvs et objekt kan være endret, men Store.update er ennå ikke kallt på det tidspunkt hvor cachingen slås på. Siden
 * relation cachingen opererer synkront når den er enablet, virker det riktigst at algoritmen  for enabling av
 * cachen tar hensyn til disse  objektene også.
 * <p/>
 * Ovenstående prinsipper bør testes for klient og server. Det finnes mange kombinasjoner så ikke alle blir nødvendigvis
 * testet. Fokus har vært på å få testet de mest vanlige patterns på klient og server.
 *
 * @author Henrik Fredholm
 * @since 2.7
 */
@Test(groups = "singlevm-required")
public class StoreRelationCacheTest extends StoreTestMixedTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store storeClient;

    @Inject
    X1AAFinderService x1AAFinderService;

    @Inject
    private DBLockerService dbLockerService;

    @BeforeMethod
    protected void evictAll() {
        storeClient.getRelationCache().setEnabled(false);
        storeClient.evictAll();
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

    public void testStoreSessionClientRelationCachingDisabledByDefault() {
        assertFalse(storeClient.getRelationCache().isEnabled());
    }

    public void testStoreSessionClientRelationCachingKanEnablesOgDisables() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        storeClient.getRelationCache().setEnabled(true);
        assertTrue(storeClient.getRelationCache().isEnabled());
        storeClient.getRelationCache().setEnabled(false);
        assertFalse(storeClient.getRelationCache().isEnabled());
    }

    public void testClientUnitOfWorkArverRelationCachingFraUndeliggende() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            assertFalse(storeClient.getRelationCache().isEnabled());
        }
        storeClient.getRelationCache().setEnabled(true);
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            assertTrue(storeClient.getRelationCache().isEnabled());
        }
    }

    public void testStoreSessionServerRelationCachingDisabledByDefault() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                assertFalse(serverStore.getRelationCache().isEnabled());
                return null;
            }
        });
    }

    public void testStoreSessionServerRelationCachingKanEnablesOgDisables() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                assertFalse(serverStore.getRelationCache().isEnabled());
                serverStore.getRelationCache().setEnabled(true);
                assertTrue(serverStore.getRelationCache().isEnabled());
                serverStore.getRelationCache().setEnabled(false);
                assertFalse(serverStore.getRelationCache().isEnabled());
                return null;
            }
        });
    }

    public void testServerUnitOfWorkArverRelationCachingFraUndeliggende() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                assertFalse(serverStore.getRelationCache().isEnabled());
                try (UnitOfWork ignore = serverStore.beginUnitOfWork()) {
                    assertFalse(serverStore.getRelationCache().isEnabled());
                }
                serverStore.getRelationCache().setEnabled(true);
                try (UnitOfWork ignore = serverStore.beginUnitOfWork()) {
                    assertTrue(serverStore.getRelationCache().isEnabled());
                }
                return null;
            }
        });
    }

    public void testClientUnitOfWorkRevertesEtterUnitOfWorkErAvsluttet() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            assertTrue(storeClient.getRelationCache().isEnabled());
        }
        assertFalse(storeClient.getRelationCache().isEnabled());

        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                storeClient.getRelationCache().setEnabled(true);
                assertTrue(storeClient.getRelationCache().isEnabled());
                storeClient.commitUnitOfWork(inner);
            }
            assertFalse(storeClient.getRelationCache().isEnabled());
        }
        assertFalse(storeClient.getRelationCache().isEnabled());
    }

    public void testClientCachingIUnitOfWorkKanDisablesNaaCachingIUnderliggerErPaa() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            assertTrue(storeClient.getRelationCache().isEnabled());

            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                assertTrue(storeClient.getRelationCache().isEnabled());
                storeClient.getRelationCache().setEnabled(false);
                assertFalse(storeClient.getRelationCache().isEnabled());
                storeClient.commitUnitOfWork(inner);
            }

            assertTrue(storeClient.getRelationCache().isEnabled());

        }
        assertFalse(storeClient.getRelationCache().isEnabled());
    }


    public void testStoreSessionClientLockObjectMedEtterfoelgedeRelationCachingEnabling() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        storeClient.lock(a1Id);
        // RelationCache onEnable-algoritmen vil se a1. men ikke ta hensyn til det siden det ikke er lov å endre det uten for en UnitOfWork
        storeClient.getRelationCache().setEnabled(true);
        storeClient.unlock(a1Id);
    }


    /**
     * Tester at relation cachen beregner relasjoner riktig når den enables etter at objekter i Store er endret. I
     * testcasen står starter vi men en situasjon hvor a1->b2Id. Så endres a1->bNewId. Så lenge caching ikke er
     * enablet vil servicen lese det som står i databasen. Dvs for b2Id finner vi {a1Id} og for bNewId finner vi {}.
     * Når cachen enables må algoritmen innse at a1 er endret. Dvs den gamel versjon av a1 (hvor a1->b2Id) gjelder ikke
     * lengre. Istedet gjelder den oppdaterte versjonen av a1 (a1->bNewId). Algoritmen må innse at koblingen a1->b2Id er
     * 'removed' og koblingen a1->bNewId er 'added'
     * <p/>
     * <p>Videre, etter at UnitOfWork er aborted skal relasjonene svare det opprinnelige
     */
    public void testClientUpdateMedEtterfoelgedeRelationCachingEnabling() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        storeClient.lock(a1Id);
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            X1AA a1 = storeClient.get(a1Id);
            X1BBOne bNew = new X1BBOne();
            storeClient.insert(bNew);
            X1BBOneId<?> bNewId = bNew.getId();
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsBefore = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsBefore.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsBefore.get(b2Id), Collections.singleton(a1Id));
            a1.setSomeBBId(bNewId);
            storeClient.update(a1);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsAfterChange.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsAfterChange.get(b2Id), Collections.singleton(a1Id));
            storeClient.getRelationCache().setEnabled(true);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsAfterEnabled.get(bNewId), Collections.singleton(a1Id));
            assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());
        }
        assertEquals(storeClient.get(a1Id).getSomeBBId(), b2Id);
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
        assertEquals(invSomeBBIds.get(b2Id), Collections.singleton(a1Id));
        storeClient.unlock(a1Id);
    }

    /**
     * Denne testen er det samme som {@link #testClientUpdateMedEtterfoelgedeRelationCachingEnabling()} med den tvist
     * at update ikke blir kall. Cachingen skal likevel få det riktig
     */
    public void testClientUpdateUtenFaktiskUpdateMedEtterfoelgedeRelationCachingEnabling() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        storeClient.lock(mockupFacade.getX1AAMockupFactory().getA1Id());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            X1AA a1 = storeClient.get(mockupFacade.getX1AAMockupFactory().getA1Id());
            X1BBOne bNew = new X1BBOne();
            storeClient.insert(bNew);
            X1BBOneId<?> bNewId = bNew.getId();
            X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsBefore = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsBefore.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsBefore.get(b2Id), Collections.singleton(a1.getId()));
            a1.setSomeBBId(bNewId);
            // Kalles ikke: clientStore.update(a1);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsAfterChange.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsAfterChange.get(b2Id), Collections.singleton(a1.getId()));
            storeClient.getRelationCache().setEnabled(true);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(bNewId, b2Id));
            assertEquals(invSomeBBIdsAfterEnabled.get(bNewId), Collections.singleton(a1.getId()));
            assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());
        }
        storeClient.unlock(mockupFacade.getX1AAMockupFactory().getA1Id());
    }

    /**
     * Denne testen er det samme som {@link #testClientUpdateMedEtterfoelgedeRelationCachingEnabling()} med den tvist
     * at relasjonen settes til null (a1->null).
     */
    public void testClientUpdateTilNullMedEtterfoelgedeRelationCachingEnabling() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        storeClient.lock(mockupFacade.getX1AAMockupFactory().getA1Id());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            X1AA a1 = storeClient.get(mockupFacade.getX1AAMockupFactory().getA1Id());
            X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsBefore = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsBefore.get(b2Id), Collections.singleton(a1.getId()));
            a1.setSomeBBId(null);
            storeClient.update(a1);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsAfterChange.get(b2Id), Collections.singleton(a1.getId()));
            storeClient.getRelationCache().setEnabled(true);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());
        }
        storeClient.unlock(mockupFacade.getX1AAMockupFactory().getA1Id());
    }

    /**
     * Tester at relation cachen beregner relasjoner riktig når den enables etter at objekter i Store er slettet. I
     * testcasen står starter vi men en situasjon hvor a1->b2Id. Så a1. Så lenge caching ikke er
     * enablet vil servicen lese det som står i databasen. Dvs for b2Id finner vi {a1Id}. Når cachen enables må
     * algoritmen innse at a1 er slettet. Dvs den gamel versjon av a1 (hvor a1->b2Id) gjelder ikke
     * lengre. Algoritmen må innse at koblingen a1->b2Id er 'removed'
     */
    public void testClientDeleteMedEtterfoelgedeRelationCachingEnabling() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        storeClient.lock(a1Id);
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            X1AA a1 = storeClient.get(a1Id);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsBefore = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsBefore.get(b2Id), Collections.singleton(a1Id));
            storeClient.delete(a1);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsAfterChange.get(b2Id), Collections.singleton(a1Id));
            storeClient.getRelationCache().setEnabled(true);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
            assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());
        }
        assertEquals(storeClient.get(a1Id).getSomeBBId(), b2Id);
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
        assertEquals(invSomeBBIds.get(b2Id), Collections.singleton(a1Id));
        storeClient.unlock(a1Id);
    }

    /**
     * Tester at relation cachen beregner relasjoner riktig når den enables etter at objekter i Store er endret en en
     * ytre unit of work.
     * <p/>
     * I testcasen starter vi men en situasjon hvor a1->b2Id. Så endres a1->bNewId i ytre unit of work og til
     * a1-b2NewId i indre unit of work. Så lenge caching ikke er enablet vil servicen lese det som står i databasen.
     * Dvs for b2Id finner vi {a1Id} og for bNewId  og b2NewId finner vi {}.
     * Når cachen enables må algoritmen innse at a1 er endret. Dvs den gamel versjon av a1 (hvor a1->b2Id) gjelder ikke
     * lengre. Istedet gjelder den oppdaterte versjonen av a1 (a1->b2NewId). Algoritmen må innse at koblingen a1->b2Id er
     * 'removed' og koblingen a1->b2NewId er 'added', mens koblingen a1->bNewId er irrelevant.
     * <p/>
     * <p>Videre, etter at UnitOfWork er aborted skal relasjonene svare det opprinnelige
     */
    public void testClientUpdatesInNestedUnitOfWorksMedEtterfoelgedeRelationCachingEnabling() {
        assertFalse(storeClient.getRelationCache().isEnabled());
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        storeClient.lock(a1Id);
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            X1AA a1 = storeClient.get(a1Id);
            X1BBOne bNew = new X1BBOne();
            storeClient.insert(bNew);
            X1BBOneId<?> bNewId = bNew.getId();
            a1.setSomeBBId(bNewId);
            storeClient.update(a1);
            X1BBOneId<?> b2NewId = null;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                X1BBOne b2New = new X1BBOne();
                storeClient.insert(b2New);
                b2NewId = b2New.getId();
                X1AA a1Inner = storeClient.get(a1Id);
                a1Inner.setSomeBBId(b2NewId);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsBefore = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2NewId, bNewId, b2Id));
                assertEquals(invSomeBBIdsBefore.get(b2NewId), Collections.emptySet());
                assertEquals(invSomeBBIdsBefore.get(bNewId), Collections.emptySet());
                assertEquals(invSomeBBIdsBefore.get(b2Id), Collections.singleton(a1Id));
                storeClient.update(a1Inner);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2NewId, bNewId, b2Id));
                assertEquals(invSomeBBIdsAfterChange.get(b2NewId), Collections.emptySet());
                assertEquals(invSomeBBIdsAfterChange.get(bNewId), Collections.emptySet());
                assertEquals(invSomeBBIdsAfterChange.get(b2Id), Collections.singleton(a1Id));
                storeClient.getRelationCache().setEnabled(true);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2NewId, bNewId, b2Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(b2NewId), Collections.singleton(a1Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(bNewId), Collections.emptySet());
                assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());
                storeClient.commitUnitOfWork(inner);
            }
            assertEquals(storeClient.get(a1Id).getSomeBBId(), b2NewId);
            // Caching er ikke enablet får vi får det som er lagret på serveren
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsOuterUoW = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2NewId, bNewId, b2Id));
            assertEquals(invSomeBBIdsOuterUoW.get(b2NewId), Collections.emptySet());
            assertEquals(invSomeBBIdsOuterUoW.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsOuterUoW.get(b2Id), Collections.singleton(a1Id));
            storeClient.getRelationCache().setEnabled(true);
            Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsOuterUoWEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2NewId, bNewId, b2Id));
            assertEquals(invSomeBBIdsOuterUoWEnabled.get(b2NewId), Collections.singleton(a1Id));
            assertEquals(invSomeBBIdsOuterUoW.get(bNewId), Collections.emptySet());
            assertEquals(invSomeBBIdsOuterUoWEnabled.get(b2Id), Collections.emptySet());
        }
        assertEquals(storeClient.get(a1Id).getSomeBBId(), b2Id);
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsOuterUoW = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
        assertEquals(invSomeBBIdsOuterUoW.get(b2Id), Collections.singleton(a1Id));
        Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIds = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
        assertEquals(invSomeBBIds.get(b2Id), Collections.singleton(a1Id));
        storeClient.unlock(a1Id);
    }

    /**
     * Tester at enabling av relation cachen virker for objekter som bare er låst i StoreServerSession
     */
    public void testStoreSessionServerLockObjectMedEtterfoelgedeRelationCachingEnabling() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Inject
            X1AAFinderService x1AAFinderService;

            @Override
            public Object run() {
                X1AA a1 = serverStore.lock(a1Id);
                // RelationCache onEnable-algoritmen vil se a1. men ikke ta hensyn til den siden den vil være i synk
                serverStore.getRelationCache().setEnabled(true);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.singleton(a1Id));
                serverStore.unlock(a1Id);
                return null;
            }
        });
    }

    /**
     * Tester at enabling av relation cachen virker for objekter som endres uten update i StoreServerSession
     */
    public void testStoreSessionServerUpdateUtenFaktiskUpdateEtterfoelgedeRelationCachingEnabling() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private StoreServer storeServer;
            @Inject
            X1AAFinderService x1AAFinderService;

            @Override
            public Object run() {
                storeServer.get(a1Id);
                X1AA a1 = storeServer.lock(a1Id);
                a1.setSomeBBId(null);
                // RelationCache onEnable-algoritmen vil se a1, men ikke ta hensyn til den siden den vil være i synk fordi flush kalles automatisk
                storeServer.getRelationCache().setEnabled(true);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsAfterEnabled = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.emptySet());

                // Trenger ingen update her for å se endringen side relaction tracking skjer synkront.
                a1.setSomeBBId(b2Id);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsChange = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.singleton(a1Id));

                // finder kall blir fortsatt riktig siden endringen flushes til database før søk
                storeServer.getRelationCache().setEnabled(false);
                Map<X1BBOneId<?>, Set<X1AAId<?>>> invSomeBBIdsChange2 = x1AAFinderService.findInvSomeBBIds(ImmutableList.of(b2Id));
                assertEquals(invSomeBBIdsAfterEnabled.get(b2Id), Collections.singleton(a1Id));
                storeServer.update(a1);
                return null;
            }
        });
    }

    /**
     * Tester at ident cachen virker på riktig nivå, altså at når enabling av caching collecter opprinnelige relasjoner,
     * så skal Store.get() navigere på nivå 0.
     */
    public void testRiktigIdentCachingVedOppdateringAvAvledetDelAvIdentFoerRelationCachingEnabling() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        final int orgBNr = mockupFacade.getStore().get(b2Id).getNr();
        final int orgANr = mockupFacade.getStore().get(a1Id).getNr();

        Map<X1AAIdent, Set<X1AAId<?>>> x1AAIdsForIdents;

        X1AAIdent orgIdent = new X1AAIdent(orgBNr, orgANr);
        x1AAIdsForIdents = x1AAFinderService.findX1AAIdsForIdents(Collections.singleton(orgIdent));
        assertEquals(x1AAIdsForIdents.get(orgIdent), Collections.singleton(a1Id));

        try (UnitOfWork ignored = storeClient.beginUnitOfWork()) {
            X1BBOne b2 = storeClient.lock(b2Id);
            storeClient.lock(a1Id);
            b2.setNr(b2.getNr() + 10);
            storeClient.update(b2);

            X1AAIdent modIdent = new X1AAIdent(b2.getNr(), orgANr);

            storeClient.getRelationCache().setEnabled(true);
            x1AAIdsForIdents = x1AAFinderService.findX1AAIdsForIdents(ImmutableSet.of(orgIdent, modIdent));
            assertEquals(x1AAIdsForIdents.get(orgIdent), Collections.emptySet());
            assertEquals(x1AAIdsForIdents.get(modIdent), Collections.singleton(a1Id));

            assertSame(storeClient.get(b2Id), b2);
        }
    }

    // TODO: Det er fortsatt flere testcaser som bør skrives, blant annet transfer fra klient til server og update/sletting med detached objekt

}
