package no.statkart.skif.storetest.wsapi.service.store;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubble;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.UnitOfWorkTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface StoreUpdateServiceWSI extends ServiceWSI {

    StoreTestBubble lockObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    StoreTestBubbleList lockObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    void saveTransfer(@WebParam(name = "transfer") UnitOfWorkTransfer transfer, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
