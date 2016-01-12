package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAIdent;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneIdent;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertFalse;

/**
 * Tester relationcaching av identer gjennom Store, herunder samspillet med relation cachingen og Unit of Work konseptet.
 * Store støtter at relation caching kan slås av og på, dvs at søk på identer blir riktig når cachingen slås på selv
 * om det har blitt gjort endringer på objekter mens cachingen har vært slått av.
 * <p/>
 * Identer oppfører seg litt anderledes enn relasjonsverdier fordi en ident kan være sammensatt av flere felter og være
 * bygget opp av verdier fra andre objekter som nås via navigering på id gjennom Store. Dette krever ekstra støtte i
 * relasjonscachingen.
 * <p/>
 * Spesielt gjelder det at en ident som består av flere felter først er endret når alle felter er oppdatert. Dvs
 * relasjonscachen må bli fortalt når identendringen er ferdig. På det tidspunktet kan den gamle identen ikke
 * hentes ut fra objektet så hvilken ident som utgår må angis på annen vis enn å hente det fra objektet som har
 * fått ny ident.
 * <p/>
 * Videre kan en ident endre seg hvis et annet objekt som inngår i identen endre sine felter som inngår i identen.
 * Når dette skjer må relasjonscachingen bli fortalt om dette.
 * <p/>
 * Testen anvender 2 objekter X1AA og X1BBOne, hvor X1AA->X1BBOne. X1AA har ident X1AAIdent som består av 2 felter
 * hvor den ene er hentet fra et felt i X1BBOne. X1BBOne har ident X1BBOneIdent som består av dette feltet. Når X1BBOne
 * endre sitt identfelt så endre også alle X1AA som peker på X1BBOne sin ident.
 *
 * @author Henrik Fredholm
 * @since 2.7
 */
@Test(groups = "singlevm-required")
public class StoreRelationCacheIdentTest extends StoreTestMixedTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store storeClient;

    @Inject
    X1AAFinderService finderService;

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
                        mockupFacade.getX1BBOneMockupFactory().getAllIds(X1BBOneId.class)
                ));
            }
        });
    }

    private Set<X1AAId<?>> findIdent(X1AAIdent ident) {
        return finderService.findX1AAIdsForIdents(Collections.singleton(ident)).get(ident);
    }

    private Set<X1BBOneId<?>> findIdent(X1BBOneIdent ident) {
        return finderService.findX1BBOneIdsForIdents(Collections.singleton(ident)).get(ident);
    }

    public void testTestSet1() {
        getWriteMockupFacadeAndSaveDataForTestSet1();
    }

    /**
     * Tester at når et objekt får endret sin ident så må man eksplisitt fortelle cachen om det
     * via kall til onIdentChanged(). Tester også at commit fra inner unit of work virker slik
     * ident relasjoner automatisk blir riktig i out unit of work.
     */
    public void testOnClientChangeCompositeIdent() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldIdent = storeClient.get(a1Id).getIdent();
            X1AAIdent oldIdent2;
            X1AAIdent newIdent;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                X1AA a1 = storeClient.lock(a1Id);
                a1.setNr(100); // Ident endret, men ikke  relationcache for ident
                newIdent = a1.getIdent();
                assertThat(findIdent(oldIdent)).containsExactly(a1Id);
                assertThat(findIdent(newIdent)).isEmpty();
                storeClient.update(a1);
                a1.onIdentChanged();  // Angi at ident er endret
                assertThat(findIdent(oldIdent)).isEmpty();
                assertThat(findIdent(newIdent)).containsExactly(a1Id);
                oldIdent2 = newIdent;
                a1.setNr(101); // Ident endret igjen
                newIdent = a1.getIdent();
                a1.onIdentChanged();  // Angi at ident er endret
                assertThat(findIdent(oldIdent)).isEmpty();
                assertThat(findIdent(oldIdent2)).isEmpty();
                assertThat(findIdent(newIdent)).containsExactly(a1Id);
                storeClient.update(a1);
                storeClient.commitUnitOfWork(inner);
            }
            assertThat(findIdent(oldIdent)).isEmpty();
            assertThat(findIdent(oldIdent2)).isEmpty();
            assertThat(findIdent(newIdent)).containsExactly(a1Id);
        }
    }

    /**
     * Tester at ident endring i inner unit of work med cahcing slått av gir riktig resultat
     * i out unit of work som har caching på.
     */
    public void testOnClientChangeCompositeIdentWithInnerUnitOfWorkDisabled() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldIdent = storeClient.get(a1Id).getIdent();
            X1AAIdent oldIdent2;
            X1AAIdent newIdent = null;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                storeClient.getRelationCache().setEnabled(false);
                X1AA a1 = storeClient.lock(a1Id);
                a1.setNr(100); // Ident endret, men ikke  relationcache for ident
                newIdent = a1.getIdent();
                a1.onIdentChanged();  // Angi at ident er endret
                oldIdent2 = newIdent;
                a1.setNr(101); // Ident endret igjen
                newIdent = a1.getIdent();
                a1.onIdentChanged();  // Angi at ident er endret
                storeClient.update(a1);

                storeClient.getRelationCache().setEnabled(true); // Pt må dette skje manuell før commit
                assertThat(findIdent(oldIdent)).isEmpty();
                assertThat(findIdent(newIdent)).containsExactly(a1Id);
                storeClient.commitUnitOfWork(inner);
            }
            assertThat(findIdent(oldIdent)).isEmpty();
            assertThat(findIdent(oldIdent2)).isEmpty();
            assertThat(findIdent(newIdent)).containsExactly(a1Id);
        }
    }


    /**
     * Tester at når et objekt får endret sin ident og at denne inngår i en avledet ident så må man
     * eksplisitt fortelle cachen om det via kall til onIdentChanged() som kalder videre til
     * onchangedDerivedIdents. Tester også at commit fra inner unit of work virker slik
     * ident relasjoner automatisk blir riktig i out unit of work.
     */
    public void testOnClientChangeIdentWithDerivedIdents() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldA1Ident = storeClient.get(a1Id).getIdent();
            X1AAIdent newA1Ident;
            X1BBOneIdent oldB2Ident = storeClient.get(b2Id).getIdent();
            X1BBOneIdent newB2Ident;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                X1AA a1 = storeClient.get(a1Id);
                X1BBOne b2 = storeClient.lock(b2Id);
                b2.setNr(100); // Ident endret, men ikke  relationcache for ident
                newA1Ident = a1.getIdent();
                newB2Ident = b2.getIdent();
                assertThat(findIdent(oldA1Ident)).containsExactly(a1Id);
                assertThat(findIdent(oldB2Ident)).containsExactly(b2Id);
                assertThat(findIdent(newA1Ident)).isEmpty();
                assertThat(findIdent(newB2Ident)).isEmpty();
                b2.onIdentChanged();  // Angi at ident er endret
                assertThat(findIdent(oldA1Ident)).isEmpty();
                assertThat(findIdent(oldB2Ident)).isEmpty();
                assertThat(findIdent(newA1Ident)).containsExactly(a1Id);
                assertThat(findIdent(newB2Ident)).containsExactly(b2Id);
                storeClient.update(b2);
                storeClient.commitUnitOfWork(inner);
            }
            assertThat(findIdent(oldA1Ident)).isEmpty();
            assertThat(findIdent(oldB2Ident)).isEmpty();
            assertThat(findIdent(newA1Ident)).containsExactly(a1Id);
            assertThat(findIdent(newB2Ident)).containsExactly(b2Id);
        }
    }

    /**
     * Tester at abort fra inner unit of work virker slik ident relasjoner automatisk blir riktig i out unit of work
     * når de er endret i inner uow. Et objekt får endret sin ident som inngår i en avledet ident så må man
     * eksplisitt fortelle cachen om det via kall til onIdentChanged() som kalder videre til
     * onchangedDerivedIdents.
     */
    public void testOnClientChangeIdentWithDerivedIdentsOnAbort() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldA1Ident = storeClient.get(a1Id).getIdent();
            X1AAIdent newA1Ident;
            X1BBOneIdent oldB2Ident = storeClient.get(b2Id).getIdent();
            X1BBOneIdent newB2Ident;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                X1AA a1 = storeClient.get(a1Id);
                X1BBOne b2 = storeClient.lock(b2Id);
                b2.setNr(100); // Ident endret, men ikke  relationcache for ident
                newA1Ident = a1.getIdent();
                newB2Ident = b2.getIdent();
                assertThat(findIdent(oldA1Ident)).containsExactly(a1Id);
                assertThat(findIdent(oldB2Ident)).containsExactly(b2Id);
                assertThat(findIdent(newA1Ident)).isEmpty();
                assertThat(findIdent(newB2Ident)).isEmpty();
                b2.onIdentChanged();  // Angi at ident er endret
                assertThat(findIdent(oldA1Ident)).isEmpty();
                assertThat(findIdent(oldB2Ident)).isEmpty();
                assertThat(findIdent(newA1Ident)).containsExactly(a1Id);
                assertThat(findIdent(newB2Ident)).containsExactly(b2Id);
                storeClient.update(b2);
                storeClient.abortUnitOfWork(inner);
            }
            assertThat(findIdent(oldA1Ident)).containsExactly(a1Id);
            assertThat(findIdent(oldB2Ident)).containsExactly(b2Id);
            assertThat(findIdent(newA1Ident)).isEmpty();
            assertThat(findIdent(newB2Ident)).isEmpty();
        }
    }

    /**
     * Tester av cache når identendringer er utført med caching disabled i inner uow. Et objekt med avledede
     * identer får endret sin ident. I outer unit of work er caching enabled. Ident søk med gamle identer skal da gi
     * null mens ident søk med ny ident skal gi objekt etter commit i inner uow.
     */
    public void testOnClientChangeIdentWithDerivedIdentsWithCachingDisbledInInnerUOWOnCommit() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldA1Ident = storeClient.get(a1Id).getIdent();
            X1AAIdent newA1Ident;
            X1BBOneIdent oldB2Ident = storeClient.get(b2Id).getIdent();
            X1BBOneIdent newB2Ident;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                storeClient.getRelationCache().setEnabled(false);
                X1AA a1 = storeClient.get(a1Id);
                X1BBOne b2 = storeClient.lock(b2Id);
                b2.setNr(100); // Ident endret, men ikke  relationcache for ident
                newA1Ident = a1.getIdent();
                newB2Ident = b2.getIdent();
                b2.onIdentChanged();
                storeClient.update(b2);
                storeClient.commitUnitOfWork(inner);
            }
            assertThat(findIdent(oldA1Ident)).isEmpty();
            assertThat(findIdent(oldB2Ident)).isEmpty();
            assertThat(findIdent(newA1Ident)).containsExactly(a1Id);
            assertThat(findIdent(newB2Ident)).containsExactly(b2Id);
        }
    }

    /**
     * Tester caching når identendringer er utført med caching disabled i inner uow. Et objekt med avledede
     * identer får endret sin ident. I outer unit of work er caching enabled. Ident søk med gamle identer skal da gi
     * opprinnelig objecter  mens ident søk med ny ident skal gi null etter abort av inner uow
     */
    public void testOnClientChangeIdentWithDerivedIdentsWithCachingDisbledInInnerUOWOnAbort() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        final X1BBOneId<?> b2Id = mockupFacade.getX1BBOneMockupFactory().getB2Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldA1Ident = storeClient.get(a1Id).getIdent();
            X1AAIdent newA1Ident;
            X1BBOneIdent oldB2Ident = storeClient.get(b2Id).getIdent();
            X1BBOneIdent newB2Ident;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                storeClient.getRelationCache().setEnabled(false);
                X1AA a1 = storeClient.get(a1Id);
                X1BBOne b2 = storeClient.lock(b2Id);
                b2.setNr(100); // Ident endret, men ikke  relationcache for ident
                newA1Ident = a1.getIdent();
                newB2Ident = b2.getIdent();
                b2.onIdentChanged();
                storeClient.update(b2);
                storeClient.abortUnitOfWork(inner);
            }
            assertThat(findIdent(newA1Ident)).isEmpty();
            assertThat(findIdent(newB2Ident)).isEmpty();
            assertThat(findIdent(oldA1Ident)).containsExactly(a1Id);
            assertThat(findIdent(oldB2Ident)).containsExactly(b2Id);
        }
    }

    /**
     * Tester caching når identendringer er utført med caching disabled i inner uow. Et objekt med avledede
     * identer får endret sin ident. I outer unit of work er caching enabled. Ident søk med gamle identer skal da gi
     * opprinnelig objecter  mens ident søk med ny ident skal gi null etter abort av inner uow
     */
    /**
     * Tester at når et objekt får endret sin ident så må man eksplisitt fortelle cachen om det
     * via kall til onIdentChanged(). Tester også at commit fra inner unit of work virker slik
     * ident relasjoner automatisk blir riktig i out unit of work.
     */
    public void testOnClientChangeAndRemoveCompositeIdent() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final X1AAId<?> a1Id = mockupFacade.getX1AAMockupFactory().getA1Id();
        assertFalse(storeClient.getRelationCache().isEnabled());
        try (UnitOfWork ignore = storeClient.beginUnitOfWork()) {
            storeClient.getRelationCache().setEnabled(true);
            X1AAIdent oldIdent = storeClient.get(a1Id).getIdent();
            X1AAIdent newIdent;
            try (UnitOfWork inner = storeClient.beginUnitOfWork()) {
                X1AA a1 = storeClient.lock(a1Id);
                a1.setNr(100); // Ident endret, men ikke  relationcache for ident
                newIdent = a1.getIdent();
                assertThat(findIdent(oldIdent)).containsExactly(a1Id);
                assertThat(findIdent(newIdent)).isEmpty();
                a1.onIdentChanged();  // Angi at ident er endret
                assertThat(findIdent(oldIdent)).isEmpty();
                assertThat(findIdent(newIdent)).containsExactly(a1Id);
                storeClient.update(a1);
                storeClient.delete(a1);
                storeClient.commitUnitOfWork(inner);
            }
            assertThat(findIdent(oldIdent)).isEmpty();
            assertThat(findIdent(newIdent)).isEmpty();
        }
    }

}
