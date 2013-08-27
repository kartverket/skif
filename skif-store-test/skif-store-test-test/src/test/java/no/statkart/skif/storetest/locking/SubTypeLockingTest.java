package no.statkart.skif.storetest.locking;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitiveId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubbleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester at låsing virker med subtyper.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
@Test
public class SubTypeLockingTest extends StoreTestServerTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Provider<LockerStrategy> lockerStrategyProvider;

    @Inject
    private DBLockerService dbLockerService;

    @Inject
    private Configuration configuration;

    public void testSubtypeLockSupertypeIsLocked() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Long idValue = (Long) mockupFacade.getIdService().getNextIdValue(SubTypedBubbleId.class);

        LockerStrategy lockerStrategy = lockerStrategyProvider.get();

        lockerStrategy.lock(new SubTypeWithPrimitiveId(idValue));

        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypeWithPrimitiveId(idValue)), "Objektet ble ikke låst i det hele tatt");
        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypedBubbleId(idValue)), "Objektet er ikke låst som sin supertype");
    }

    public void testSupertypeLockSubtypeIsLocked() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Long idValue = (Long) mockupFacade.getIdService().getNextIdValue(SubTypedBubbleId.class);

        LockerStrategy lockerStrategy = lockerStrategyProvider.get();

        lockerStrategy.lock(new SubTypeWithPrimitiveId(idValue));

        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypedBubbleId(idValue)), "Objektet ble ikke låst i det hele tatt");
        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypeWithPrimitiveId(idValue)), "Objektet er ikke låst som sin supertype");
    }

    public void testOtherLockDifferentType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Long idValue = (Long) mockupFacade.getIdService().getNextIdValue(SubTypedBubbleId.class);
        long timeout = configuration.getLong(SkifConfigConstants.LOCK_TIMEOUT);

        dbLockerService.lock(new LockKey<Long>(SubTypedBubbleId.class.getName(), idValue), "fiktivbruker", timeout);

        LockerStrategy lockerStrategy = lockerStrategyProvider.get();

        try {
            lockerStrategy.lock(new SubTypeWithPrimitiveId(idValue));
            Assert.fail("Skulle ikke fått låst dette objektet");
        } catch (LockedException e) {
            // Dette er det som skal skje
        }
    }
}
