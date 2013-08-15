package no.statkart.skif.storetest.domain.relation;

import com.google.inject.Inject;
import no.statkart.skif.store.service.StoreService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1A;
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
public class RelationMockupFacadeFactoryTest extends StoreTestTestCase {
    @Inject
    RelationMockupFacadeFactory mockupFacadeFactory;

    @Inject StoreService storeService;

    @Test(invocationCount = 2) // Kjør to ganger for å teste at readset bare forsøkes skrives ned én gang
    public void readSet() {
        RelationMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        checkMockupDataSet(mockupFacade);
    }

    public void writeSet() {
        RelationMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacadeAndSaveData();
        checkMockupDataSet(mockupFacade);
    }

    private void checkMockupDataSet(RelationMockupFacade mockupFacade) {
        X1A a1 = storeService.getObject(mockupFacade.getX1AMockupFactory().getA1Id());
        Assert.assertEquals(a1.getId(), mockupFacade.getX1AMockupFactory().getA1Id());
        Assert.assertEquals(a1.getNr(), 1);
    }
}
