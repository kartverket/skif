package no.statkart.skif.storetest.wsapi.service.kodeliste;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface KodelisteServiceWSI extends ServiceWSI {

    KodelisteTransfer getKodelister(@WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

}
