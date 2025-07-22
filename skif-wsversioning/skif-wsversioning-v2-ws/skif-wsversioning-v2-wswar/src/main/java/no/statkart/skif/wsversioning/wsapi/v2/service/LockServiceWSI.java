package no.statkart.skif.wsversioning.wsapi.v2.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleIdList;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.LockService LockService}.
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
