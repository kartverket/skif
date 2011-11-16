package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
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

    public void testLostLocks() {
        try {
            lockingTestService.loseALock();
        } catch (OperationalException e) {
            assertTrue(e.getMessage().contains("Låsene ble borte under fullføring av brukstilfellet"));
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }

    public void testNonTransactionalLockingFail() {
        try {
            final FooId id100 = new FooId(100L);
            final FooId id101 = new FooId(101L);

            lockingTestService.lock(id100);

            try {
                lockingTestService.nonTransactionalLockingFail(id101);
            } catch (ImplementationException e) {
                assertEquals("TestABC123", e.getMessage(), "Feil exception");
            }

            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet er ikke lenger låst");
            assertFalse(lockingTestService.isLockedByMe(id101), "Objektet forble låst");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }

    public void testNonTransactionalUnlockingFail() {
        try {
            final FooId id100 = new FooId(100L);
            final FooId id101 = new FooId(101L);

            lockingTestService.lock(id100);
            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet ble ikke låst");
            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet ble låst opp av låsesjekk");

            try {
                lockingTestService.nonTransactionalUnlockingFail(id100, id101);
            } catch (ImplementationException e) {
                assertEquals("TestABC123", e.getMessage(), "Feil exception");
            }

            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet ble låst opp likevel");
            assertFalse(lockingTestService.isLockedByMe(id101), "Objektet ble ikke låst opp");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }

    public void testNonTransactionalUnlocking() {
        try {
            final FooId id100 = new FooId(100L);
            final FooId id101 = new FooId(101L);

            lockingTestService.lock(id100);

            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet ble ikke låst");
            assertTrue(lockingTestService.isLockedByMe(id100), "Objektet ble låst opp av låsesjekk");

            lockingTestService.nonTransactionalUnlocking(id100, id101);

            assertFalse(lockingTestService.isLockedByMe(id100), "Objektet ble ikke låst opp");
            assertFalse(lockingTestService.isLockedByMe(id101), "Objektet ble ikke låst opp");
        } finally {
            lockingTestService.releaseAllLocks();
        }
    }
}
