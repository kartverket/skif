package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.inject.Inject;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.mockup.X1AMockupFactory;
import no.statkart.skif.storetest.mockup.X1BOneMockupFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Henrik Fredholm
 */
@Test(groups = "singlevm-required")
public class UnidirectionalWithoutComponentsTest extends StoreTestTestCase {
    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    Store store;


    public void testGetRelationToOne() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        X1AMockupFactory x1AMockupFactory = mockupFacade.getX1AMockupFactory();
        X1BOneMockupFactory x1BOneMockupFactory = mockupFacade.getX1BOneMockupFactory();

        X1A a1 = store.get(x1AMockupFactory.getA1Id());
        assertNotNull(a1);
        assertEquals(a1.getbId(),x1BOneMockupFactory.getB2Id());

        // Sjekk at a2 og a3 peker på samme B
        X1A a2 = store.get(x1AMockupFactory.getA2Id());
        assertNotNull(a2);
        assertEquals(a2.getbId(),x1BOneMockupFactory.getB3Id());

        X1A a3 = store.get(x1AMockupFactory.getA2Id());
        assertNotNull(a3);
        assertEquals(a3.getbId(),x1BOneMockupFactory.getB3Id());
    }

    public void testGetMany() {

    }

    public void testGetUnique() {

    }

    public void testManyToMany() {

    }
}
