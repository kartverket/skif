package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleIdList;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleList;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.StoreService}.
 *
 * @author Tor Egil R. Strand
 */
public interface LockServiceWSI extends ServiceWSI {

    WSVersioningBubble lock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList lockForList(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    void unlock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    void unlockForList(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    boolean isLocked(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

}
