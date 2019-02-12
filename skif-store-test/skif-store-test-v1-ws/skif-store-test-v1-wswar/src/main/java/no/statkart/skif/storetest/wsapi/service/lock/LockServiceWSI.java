package no.statkart.skif.storetest.wsapi.service.lock;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

public interface LockServiceWSI extends ServiceWSI {
StoreTestBubble lock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    StoreTestBubbleList lockForList(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    void unlock(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

    void unlockForList(StoreTestBubbleIdList ids, StoreTestContext context) throws ServiceException;

    boolean isLocked(StoreTestBubbleId id, StoreTestContext context) throws ServiceException;

}