package no.statkart.skif.storetest.wsapi.service.kodeliste;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface KodelisteServiceWSI extends ServiceWSI {
    public KodelisteIdList getKodelisteIds(@WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public KodelisteTransfer getKodelister(@WebParam(name = "context") StoreTestContext context) throws ServiceException;
}
