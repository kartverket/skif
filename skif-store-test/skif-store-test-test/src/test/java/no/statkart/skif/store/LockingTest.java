package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.service.locking.LockingTestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tester at låsing oppheves når man kaller transaksjonelle metoder, men ikke når man ikke gjøre det.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test
public class LockingTest extends StoreTestTestCase {
    @Inject
    LockingTestService lockingTestService;

    public void testNonTransactional() {
        try {
            final FooId id = new FooId(100L);

            lockingTestService.lock(id);

            assertTrue(lockingTestService.isLockedByMe(id), "Objektet forble ikke låst");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }

    public void testTransactional() {
        try {
            final FooId id = new FooId(100L);

            lockingTestService.lock(id);

            lockingTestService.update(100);

            assertFalse(lockingTestService.isLockedByMe(id), "Objektet er fortsatt låst");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }

    public void testTransactionalFail() {
        try {
            final FooId id100 = new FooId(100L);
            final FooId id101 = new FooId(101L);

            lockingTestService.lock(id101);

            try {
                lockingTestService.fail(100);
            } catch (ImplementationException e) {
                assertEquals("TestABC123", e.getMessage(), "Feil exception");
            }

            assertFalse(lockingTestService.isLockedByMe(id100), "Objektet er fortsatt låst");
            assertNotNull(lockingTestService.isLockedByMe(id101), "Objektet er ikke lenger låst");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }
}
