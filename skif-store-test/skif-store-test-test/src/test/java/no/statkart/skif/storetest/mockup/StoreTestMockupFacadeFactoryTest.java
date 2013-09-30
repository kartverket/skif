package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester persistering av mockupdata for read og write sett.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test(groups = "singlevm-required")
public class StoreTestMockupFacadeFactoryTest extends StoreTestTestCase {
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    StoreService storeService;

    @Test(invocationCount = 2) // Kjør to ganger for å teste at readset bare forsøkes skrives ned én gang
    public void readSet() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        checkMockupDataSet(mockupFacade);
    }

    public void writeSet() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        checkMockupDataSet(mockupFacade);
    }

    private void checkMockupDataSet(StoreTestMockupFacade mockupFacade) {
        X1AA a1 = storeService.getObject(mockupFacade.getX1AAMockupFactory().getA1Id());
        Assert.assertEquals(a1.getId(), mockupFacade.getX1AAMockupFactory().getA1Id());
        Assert.assertEquals(a1.getNr(), 1);
    }
}
