package no.statkart.skif.storetest.wsapi.service.uow;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.SimpleId;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import jakarta.jws.WebParam;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
public interface UowTestServiceWSI extends ServiceWSI {
    StoreBubbleTransfer findAndLock(StoreTestBubbleId bubbleId, StoreTestContext context) throws ServiceException;

    void updateTextInNewTransaction(SimpleId simpleId, String text, StoreTestContext context) throws ServiceException;

    int antallLaaserForBruker(StoreTestContext context) throws ServiceException;
}
