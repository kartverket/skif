package no.statkart.skif.storetest.wsapi.service.store;

import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.Nullable;
import javax.jws.WebParam;
import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface StoreUpdateServiceWSI extends ServiceWSI {

    public StoreTestBubble lockObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
    public StoreTestBubbleList lockObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
