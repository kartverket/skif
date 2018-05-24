package no.statkart.skif.storetest.wsapi.service.uow;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreBubbleTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.SimpleId;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
public interface UowTestServiceWSI extends ServiceWSI {
    StoreBubbleTransfer findAndLock(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void updateTextInNewTransaction(@WebParam(name = "simpleId") SimpleId simpleId, @WebParam(name = "text") String text, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    int antallLaaserForBruker(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
