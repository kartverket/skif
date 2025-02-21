package no.statkart.skif.storetest.service.uow;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.LockerStrategy;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.service.locker.DBLockerService;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
public class UowTestServiceImpl implements UowTestService {
    @Inject
    Store store;

    @Inject
    LockerStrategy lockerStrategy;

    @Inject
    DBLockerService lockerService;

    @Inject
    ServiceRequestContext serviceRequestContext;

    @Override
    public StoreBubbleTransfer findAndLock(BubbleId bubbleId) {
        StoreBubbleTransfer transfer = new StoreBubbleTransfer();
        transfer.add(store.lock(bubbleId));
        return transfer;
    }

    @Override
    public void updateTextInNewTransaction(SimpleId<?> simpleId, String text) {
        Preconditions.checkState(antallLaaserForBruker()==0, "Denne tjeneste kan kun kalles når bruker ikke ha tatt låser");
        Simple simple = store.lock(simpleId);
        simple.setText(text);
        store.update(simple);
    }

    @Override
    public int antallLaaserForBruker() {
        return lockerService.getLocksBy(serviceRequestContext.getUserName()).size();
    }
}
