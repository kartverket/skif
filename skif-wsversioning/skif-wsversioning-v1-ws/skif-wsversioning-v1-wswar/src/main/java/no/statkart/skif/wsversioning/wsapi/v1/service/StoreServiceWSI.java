package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.*;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.StoreService}.
 *
 * @author Tor Egil R. Strand
 */
public interface StoreServiceWSI extends ServiceWSI {

    WSVersioningBubble getObject(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList getObjects(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList getObjectsIgnoreMissing(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;


    WSVersioningBubbleIdList getVersions(WSVersioningBubbleId id, SnapshotVersion start, SnapshotVersion end, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleIdListForWSVersioningBubbleIdsMap getVersionsForList(WSVersioningBubbleIdList ids, SnapshotVersion start, SnapshotVersion end, WSVersioningContext context) throws ServiceException;


    WSVersioningBubble lock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList lockForList(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    void unlock(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    boolean isLocked(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

}
