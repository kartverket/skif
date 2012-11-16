package no.statkart.skif.store;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotLockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.PrincipalImpl;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.locker.DBLockerInTransactionService;
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

    private final TypeLiteral<DBLockerService<Long>> dbLockerServiceTypeLiteral = new TypeLiteral<DBLockerService<Long>>() {
    };

    private TransactionalLockerStrategy createTransactionalLockerStrategy(ServiceRequestContext serviceRequestContext) {
        return new TransactionalLockerStrategy(injector, injector.getInstance(Configuration.class), serviceRequestContext);
    }

    public void testIsLockedBy() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);
        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));

        TestBubbleId testId = new TestBubbleId(10L, SnapshotVersion.CURRENT);
        strategy.lock(testId);

        Assert.assertTrue(strategy.isLockedByCaller(testId));

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa2"));
        Assert.assertFalse(strategy.isLockedByCaller(testId));
        Assert.assertTrue(strategy.isLockedByOther(testId));

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        strategy.releaseAllLocks();
    }

    public void testUpdate() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);

        TestBubbleId testId = new TestBubbleId(11L, SnapshotVersion.CURRENT);

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        try {
            strategy.registerUpdated(testId);
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId);
        strategy.registerUpdated(testId);

        Assert.assertTrue(strategy.isLockedByCaller(testId));

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa2"));
        Assert.assertFalse(strategy.isLockedByCaller(testId));
        Assert.assertTrue(strategy.isLockedByOther(testId));

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        injector.getInstance(Key.get(dbLockerServiceTypeLiteral)).releaseAllLocks("ingroa");
    }

    @Test(invocationCount = 1 /*200*/)
    public void many() {
       testUpdate();
    }
    public void testRemove() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);

        TestBubbleId testId = new TestBubbleId(12L, SnapshotVersion.CURRENT);

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        try {
            strategy.registerRemoved(testId);
            Assert.fail("Har ikke låst testId så update skal feile!");
        } catch (NotLockedException e) {
        }

        strategy.lock(testId);
        strategy.registerRemoved(testId);

        Assert.assertTrue(strategy.isLockedByCaller(testId));

        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa2"));
        Assert.assertFalse(strategy.isLockedByCaller(testId));
        Assert.assertTrue(strategy.isLockedByOther(testId));

        injector.getInstance(Key.get(dbLockerServiceTypeLiteral)).releaseAllLocks("ingroa");
    }

    public void testInsert() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);

        TestBubbleId testId = new TestBubbleId(13L, SnapshotVersion.CURRENT);

        strategy.registerInserted(testId);

        Assert.assertFalse(strategy.isLockedByCaller(testId));

        injector.getInstance(Key.get(dbLockerServiceTypeLiteral)).releaseAllLocks("ingroa");
    }

    public void testUnlock() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);

        TestBubbleId testId = new TestBubbleId(14L, SnapshotVersion.CURRENT);

        strategy.lock(testId);
        Assert.assertTrue(strategy.isLockedByCaller(testId));
        strategy.unlock(testId);
        Assert.assertFalse(strategy.isLockedByCaller(testId));

        strategy.lock(testId);
        strategy.registerRemoved(testId);
        try {
            strategy.unlock(testId);
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().contains("Forsøkte å låse opp objekt som er endret"));
        }

        injector.getInstance(Key.get(dbLockerServiceTypeLiteral)).releaseAllLocks("ingroa");
    }

    public void testRenewLocksViaUpdate(){
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
        serviceRequestContext.setCallerPrincipal(new PrincipalImpl("ingroa"));
        TransactionalLockerStrategy strategy = createTransactionalLockerStrategy(serviceRequestContext);
        DBLockerService<Long> db = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        TestBubbleId testId = new TestBubbleId(14L, SnapshotVersion.CURRENT);
        long l = System.currentTimeMillis();
        db.lock(new LockKey<Long>(testId.getClass().getName(), testId.getValue()), "ingroa", 200);

        strategy.registerUpdated(testId);
        strategy.isLockedByCaller(testId);

        Collection<LockInfo<Long>> locks = db.getLocksBy("ingroa");
        Assert.assertEquals(locks.size(), 1);
        LockInfo<Long> lockInfo = locks.iterator().next();
        Assert.assertEquals(lockInfo.getOwner(), "ingroa");
        Assert.assertTrue(lockInfo.getExpires().getTime() > l + 250);

        db.releaseAllLocks("ingroa");
    }
}
