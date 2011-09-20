package no.statkart.skif.store;

import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test(groups = "singlevm-required")
public class TransactionalLockerStrategyTest extends StoreTestTestCase {

    @Test
    public void testIsLockedBy() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(10), ReplicaVersion.CURRENT);
        strategy.lock(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));

        strategy.releaseAllLocks("ingroa");

    }

    @Test
    public void testUpdate() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(11), ReplicaVersion.CURRENT);

        try {
            strategy.registerUpdated(testId, "ingroa");
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId, "ingroa");
        strategy.registerUpdated(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));

        strategy.releaseAllLocksOnCommit("ingroa");
    }

    @Test
    public void testRemove() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(12), ReplicaVersion.CURRENT);

        try {
            strategy.registerRemoved(testId, "ingroa");
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId, "ingroa");
        strategy.registerRemoved(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));

        strategy.releaseAllLocksOnCommit("ingroa");
    }

    @Test
    public void testInsert() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(13), ReplicaVersion.CURRENT);

        strategy.registerInserted(testId);

        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa"));

        strategy.releaseAllLocksOnCommit("ingroa");
    }

    @Test
    public void testUnlock() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(14), ReplicaVersion.CURRENT);

        strategy.lock(testId, "ingroa");
        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        strategy.unlock(testId, "ingroa");
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa"));

        strategy.lock(testId, "ingroa");
        strategy.registerRemoved(testId, "ingroa");
        strategy.unlock(testId, "ingroa");
        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));

        strategy.releaseAllLocksOnCommit("ingroa");
    }

    @Test
    public void testRenewLocksViaUpdate(){
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);
        DBLockerService<Long> db = injector.getInstance(DBLockerService.class);

        TestBubbleId testId = new TestBubbleId(new Long(14), ReplicaVersion.CURRENT);
        long l = System.currentTimeMillis();
        db.lock(new LockKey<Long>(testId.getClass().getName(), (Long) testId.getValue()), "ingroa", 200);

        strategy.registerUpdated(testId, "ingroa");
        strategy.isLockedBy(testId, "ingroa");

        Collection<LockInfo<Long>> locks = db.getLocksBy("ingroa");
        Assert.assertEquals(locks.size(), 1);
        LockInfo<Long> lockInfo = locks.iterator().next();
        Assert.assertEquals(lockInfo.getOwner(), "ingroa");
        Assert.assertTrue(lockInfo.getExpires().getTime() > l + 250);

        strategy.releaseAllLocksOnCommit("ingroa");
    }
}
