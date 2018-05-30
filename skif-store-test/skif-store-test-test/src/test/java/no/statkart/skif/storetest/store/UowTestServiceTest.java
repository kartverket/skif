package no.statkart.skif.storetest.store;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.ObjectsNotFoundException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.store.StoreClient;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRef;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SubTypeWithCollection;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.uow.UowTestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

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