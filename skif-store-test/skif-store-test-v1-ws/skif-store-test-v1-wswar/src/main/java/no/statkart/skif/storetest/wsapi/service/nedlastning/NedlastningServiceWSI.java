package no.statkart.skif.storetest.wsapi.service.nedlastning;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Bobleklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface NedlastningServiceWSI extends ServiceWSI {

    StoreTestBubbleIdList findIdsEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "Bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    StoreTestBubbleList findObjekterEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "Bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    Kontroll calcObjektkontrollForRange(@WebParam(name = "fraId") StoreTestBubbleId fraId, @WebParam(name = "tilId") StoreTestBubbleId tilId, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    Kontroll calcObjektkontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
