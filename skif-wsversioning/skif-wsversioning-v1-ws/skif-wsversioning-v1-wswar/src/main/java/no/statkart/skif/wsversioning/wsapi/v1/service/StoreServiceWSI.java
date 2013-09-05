package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;
import no.statkart.skif.wsversioning.wsapi.v1.domain.*;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.StoreService}.
 *
 * @author Tor Egil R. Strand
 */
public interface StoreServiceWSI extends ServiceWSI {
    public WSVersioningBubble getObject(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;
    public WSVersioningBubbleList getObjects(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;
    public WSVersioningBubbleList getObjectsIgnoreMissing(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    public WSVersioningBubbleIdList getVersions(WSVersioningBubbleId id, SnapshotVersion start, SnapshotVersion end, WSVersioningContext context) throws ServiceException;
    public WSVersioningBubbleIdListForWSVersioningBubbleIdsMap getVersionsForList(WSVersioningBubbleIdList ids, SnapshotVersion start, SnapshotVersion end, WSVersioningContext context) throws ServiceException;

    public WSVersioningBubble lock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;
    public void unlock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;
    public boolean isLocked(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;
}
