package no.statkart.skif.storetest.wsapi.service.endringslogg;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Bobleklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.EndringId;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringer;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Thomas Berg
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface EndringsloggServiceWSI extends ServiceWSI {

    EndringId findSisteEndringId(@WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    Endringer findEndringer(@WebParam(name = "id") EndringId id, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "retunerBobler") ReturnerBobler retunerBobler, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    Kontroll calcEndringskontroll(@WebParam(name = "id") EndringId id, @WebParam(name = "bobleklasse") Bobleklasse domeneklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    Kontroll calcObjektkontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
