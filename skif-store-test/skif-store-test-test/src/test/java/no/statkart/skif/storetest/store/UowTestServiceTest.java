package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.uow.UowTestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Tester for UowTestServiceTest
 * @author Henrik Fredholm
 * @since 2.9
 */
@Test
public class UowTestServiceTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;
    @Inject
    private StoreClient store;
    @Inject
    private UowTestService uowTestService;


    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(mockupFacade -> mockupFacade.getSimpleMockupFactory().getAllIds(SimpleId.class));
    }

    @Test
    public void antallLaaserForBruker() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        try (UnitOfWork unitOfWork = store.beginUnitOfWork()) {
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            store.lock(simple1Id);
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            store.abortUnitOfWork(unitOfWork);
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }

    @Test
    public void updateTextInNewTransaction() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        assertThat(store.get(simple1Id).getText()).isEqualTo("Ingen BubbleWithRelation peker til denne");
        uowTestService.updateTextInNewTransaction(simple1Id, "updated");
        store.evict(simple1Id);
        assertThat(store.get(simple1Id).getText()).isEqualTo("updated");
    }

    @Test
    public void updateTextInNewTransactionFailsWhenUserHasLocks() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        try (UnitOfWork unitOfWork = store.beginUnitOfWork()) {
            store.lock(simple1Id);
            try {
                uowTestService.updateTextInNewTransaction(simple1Id, "updated");
                failBecauseExceptionWasNotThrown(ImplementationException.class);
            } catch (ImplementationException e) {
                assertThat(e.getMessage()).isEqualTo("Denne tjeneste kan kun kalles når bruker ikke ha tatt låser");
            }
        }
    }

    @Test
    public void findAndLock() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        SimpleId<?> simple1Id = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        StoreBubbleTransfer transfer = uowTestService.findAndLock(simple1Id);
        assertThat(transfer.getBubbleObjects()).hasSize(1);
        assertThat(transfer.getBubbleObjects().get(simple1Id)).isNotNull();
        assertThat(transfer.getLockedIds()).containsExactly(simple1Id);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
    }
}