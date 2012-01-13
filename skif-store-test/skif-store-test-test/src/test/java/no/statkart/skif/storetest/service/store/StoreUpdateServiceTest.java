package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.persistence.FooFinder;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreUpdateServiceTest extends StoreTestTestCase {
    public static FooId<Foo> FooId_100 = new FooId<Foo>(100L);
    public static FooId<Foo> FooId_101 = new FooId<Foo>(100L);
    @Inject
    StoreUpdateService storeUpdateService;

    public void testLockObject() {
        Foo foo_100 = storeUpdateService.lock(FooId_100);
        assertEquals(foo_100.getId(), FooId_100);
    }
}
