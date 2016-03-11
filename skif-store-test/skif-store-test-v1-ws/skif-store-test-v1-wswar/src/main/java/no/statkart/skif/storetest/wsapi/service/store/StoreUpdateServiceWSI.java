package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface StoreUpdateServiceWSI extends ServiceWSI {

    StoreTestBubble lockObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    StoreTestBubbleList lockObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void saveTransfer(@WebParam(name = "transfer") UnitOfWorkTransfer transfer, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
