package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCManyId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * TODO: Dette bør nok stå et annet sted som dokumentasjon også. Det står her for å gi er overblikk av hva som bør testes her
 *
 * Tester relationcaching gjennom store, herunder samspillet med relation cachingen og Unit of Work konseptet. Store
 * støtter at relation caching kan slås av og på, dog pt med den begrensningen at caching ikke kan slås av hvis
 * den er slått på for underliggende Unit of Work.
 *
 * Et sentralt konsept i Unit of Work konseptet er at de endringer som gjøres innefor en unit of work begrenses til denne
 * i en vis forstand. Med hensyn til transaksjoner er det at endringer committes til underliggende session ved commit og
 * at endrngene rulles tilbake (dvs ikke påvirker underliggende lag) ved abort. For caching betyder det at cachet
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
 *
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
    private Store clientStore;

    @Inject
    private DBLockerService dbLockerService;

    @BeforeMethod
    protected void evictAll() {
        clientStore.getRelationCache().setEnabled(false);
        clientStore.evictAll();
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
        assertFalse(clientStore.getRelationCache().isEnabled());
    }

    public void testStoreSessionClientRelationCachingKanEnablesOgDisables() {
        assertFalse(clientStore.getRelationCache().isEnabled());
        clientStore.getRelationCache().setEnabled(true);
        assertTrue(clientStore.getRelationCache().isEnabled());
        clientStore.getRelationCache().setEnabled(false);
        assertFalse(clientStore.getRelationCache().isEnabled());
    }

    public void testClientUnitOfWorkArverRelationCachingFraUndeliggende() {
        assertFalse(clientStore.getRelationCache().isEnabled());
        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            assertFalse(clientStore.getRelationCache().isEnabled());
        }
        clientStore.getRelationCache().setEnabled(true);
        try (UnitOfWork ignore = clientStore.beginUnitOfWork()) {
            assertTrue(clientStore.getRelationCache().isEnabled());
        }
    }

    public void testStoreSessionClientLockObjectMedEtterfoelgedeRelationCachingEnabling() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        clientStore.lock(simpleId1);
        // RelationCache onEnable-algoritmen vil se simple1. men ikke ta hensyn til det siden det ikke er lov å endre det uten for en UnitOfWork
        clientStore.getRelationCache().setEnabled(true);
        clientStore.unlock(simpleId1);
    }
}
