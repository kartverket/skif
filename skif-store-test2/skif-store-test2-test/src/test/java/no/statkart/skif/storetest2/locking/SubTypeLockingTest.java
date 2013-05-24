package no.statkart.skif.storetest2.locking;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.storetest2.domain.subtype.SubTypeWithPrimitiveId;
import no.statkart.skif.storetest2.domain.subtype.SubTypedBubbleId;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester at låsing virker med subtyper.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
@Test
public class SubTypeLockingTest extends StoreTest2ServerTestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Provider<LockerStrategy> lockerStrategyProvider;

    public void testSubtypeLockSupertypeIsLocked() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Long idValue = (Long) mockupFacade.getIdService().getNextIdValue(SubTypedBubbleId.class);

        LockerStrategy lockerStrategy = lockerStrategyProvider.get();

        lockerStrategy.lock(new SubTypeWithPrimitiveId(idValue));

        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypeWithPrimitiveId(idValue)), "Objektet ble ikke låst i det hele tatt");
        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypedBubbleId(idValue)), "Objektet er ikke låst som sin supertype");
    }

    public void testSupertypeLockSubtypeIsLocked() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        Long idValue = (Long) mockupFacade.getIdService().getNextIdValue(SubTypedBubbleId.class);

        LockerStrategy lockerStrategy = lockerStrategyProvider.get();

        lockerStrategy.lock(new SubTypeWithPrimitiveId(idValue));

        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypedBubbleId(idValue)), "Objektet ble ikke låst i det hele tatt");
        Assert.assertTrue(lockerStrategy.isLockedByCaller(new SubTypeWithPrimitiveId(idValue)), "Objektet er ikke låst som sin supertype");
    }
}
