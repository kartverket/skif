package no.statkart.skif.storetest.service.locking;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.storetest.domain.demo.FooId;

/**
 * Utfører diverse operasjoner som skal føre til låsing og opplåsing av låser.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class LockingTestServiceImpl implements LockingTestService {
    @Inject
    LockerStrategy lockerStrategy;

    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public void lock(BubbleId bubbleId) {
        lockerStrategy.lock(bubbleId, serviceRequestContext.getUserName());
    }

    @Override
    public void update(int importantNumber) {
        // Ingenting å gjøre her. Det viktige er at rammeverket låser opp alle brukerens låser.
    }

    @Override
    public void fail(int badNumber) {
        lockerStrategy.lock(new FooId(100L), serviceRequestContext.getUserName());
        throw new ImplementationException("TestABC123");
    }

    @Override
    public boolean isLockedByMe(BubbleId bubbleId) {
        return lockerStrategy.isLockedBy(bubbleId, serviceRequestContext.getUserName());
    }

    @Override
    public void releaseAllLocks() {
        lockerStrategy.releaseAllLocks(serviceRequestContext.getUserName());
    }
}
