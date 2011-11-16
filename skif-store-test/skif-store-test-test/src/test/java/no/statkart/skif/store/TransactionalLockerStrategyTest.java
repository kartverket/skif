package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test(groups = "singlevm-required")
public class TransactionalLockerStrategyTest extends StoreTestTestCase {

    public void testIsLockedBy() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(10), SnapshotVersion.CURRENT);
        strategy.lock(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));
        Assert.assertTrue(strategy.isLockedByOther(testId, "ingroa2"));

        strategy.releaseAllLocks("ingroa");
    }

    public void testUpdate() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(11), SnapshotVersion.CURRENT);

        try {
            strategy.registerUpdated(testId, "ingroa");
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId, "ingroa");
        strategy.registerUpdated(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));
        Assert.assertTrue(strategy.isLockedByOther(testId, "ingroa2"));

        injector.getInstance(DBLockerService.class).releaseAllLocks("ingroa");
    }

    @Test(invocationCount = 1 /*200*/)
    public void many() {
       testUpdate();
    }
    public void testRemove() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(12), SnapshotVersion.CURRENT);

        try {
            strategy.registerRemoved(testId, "ingroa");
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId, "ingroa");
        strategy.registerRemoved(testId, "ingroa");

        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa2"));
        Assert.assertTrue(strategy.isLockedByOther(testId, "ingroa2"));

        injector.getInstance(DBLockerService.class).releaseAllLocks("ingroa");
    }

    public void testInsert() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(13), SnapshotVersion.CURRENT);

        strategy.registerInserted(testId);

        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa"));

        injector.getInstance(DBLockerService.class).releaseAllLocks("ingroa");
    }

    public void testUnlock() {
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);

        TestBubbleId testId = new TestBubbleId(new Long(14), SnapshotVersion.CURRENT);

        strategy.lock(testId, "ingroa");
        Assert.assertTrue(strategy.isLockedBy(testId, "ingroa"));
        strategy.unlock(testId, "ingroa");
        Assert.assertFalse(strategy.isLockedBy(testId, "ingroa"));

        strategy.lock(testId, "ingroa");
        strategy.registerRemoved(testId, "ingroa");
        try {
            strategy.unlock(testId, "ingroa");
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().contains("Forsøkte å låse opp objekt som er endret"));
        }

        injector.getInstance(DBLockerService.class).releaseAllLocks("ingroa");
    }

    public void testRenewLocksViaUpdate(){
        TransactionalLockerStrategy strategy = injector.getInstance(TransactionalLockerStrategy.class);
        DBLockerService<Long> db = injector.getInstance(DBLockerService.class);

        TestBubbleId testId = new TestBubbleId(new Long(14), SnapshotVersion.CURRENT);
        long l = System.currentTimeMillis();
        db.lock(new LockKey<Long>(testId.getClass().getName(), (Long) testId.getValue()), "ingroa", 200);

        strategy.registerUpdated(testId, "ingroa");
        strategy.isLockedBy(testId, "ingroa");

        Collection<LockInfo<Long>> locks = db.getLocksBy("ingroa");
        Assert.assertEquals(locks.size(), 1);
        LockInfo<Long> lockInfo = locks.iterator().next();
        Assert.assertEquals(lockInfo.getOwner(), "ingroa");
        Assert.assertTrue(lockInfo.getExpires().getTime() > l + 250);

        db.releaseAllLocks("ingroa");
    }
}
