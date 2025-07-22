package no.statkart.skif.wsversioning.wsapi.v2.service;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.wsversioning.wsapi.v2.context.WSVersioningContext;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubble;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleId;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleIdList;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleIdListForWSVersioningBubbleIdsMap;
import no.statkart.skif.wsversioning.wsapi.v2.domain.WSVersioningBubbleList;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * WebServiceInterface for {@link no.statkart.skif.wsversioning.service.StoreService StoreService}.
 *
 * @author Tor Egil R. Strand
 */
public interface StoreServiceWSI extends ServiceWSI {

    WSVersioningBubble getObject(WSVersioningBubbleId id, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList getObjects(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleList getObjectsIgnoreMissing(WSVersioningBubbleIdList ids, WSVersioningContext context) throws ServiceException;


    WSVersioningBubbleIdList getVersions(WSVersioningBubbleId id, XMLGregorianCalendar start, XMLGregorianCalendar end, WSVersioningContext context) throws ServiceException;

    WSVersioningBubbleIdListForWSVersioningBubbleIdsMap getVersionsForList(WSVersioningBubbleIdList ids, XMLGregorianCalendar start, XMLGregorianCalendar end, WSVersioningContext context) throws ServiceException;

}
