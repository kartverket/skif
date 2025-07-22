package no.statkart.skif.wsversioning.wsapi.v1.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v1.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleIdList;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleIdListForWSVersioningBubbleIdsMap;
import no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleList;
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

}
