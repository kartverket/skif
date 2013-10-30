package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.inject.Inject;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Thomas Berg
 */
@Test
public class X1AAFinderServiceTest extends StoreTestServerTestCase {
    public void testX1AAFinderService(){
    final X1AAFinderService x1AAFinderService = injector.getInstance(X1AAFinderService.class);
        Collection<X1BBOneId<?>> x1BBOneList= new ArrayList<X1BBOneId<?>>();
        Map<X1BBOneId<?>, Set<X1AAId<?>>> testMap = x1AAFinderService.findInvSomeBBIds(x1BBOneList);
        assertTrue(testMap.isEmpty());
    }


}
