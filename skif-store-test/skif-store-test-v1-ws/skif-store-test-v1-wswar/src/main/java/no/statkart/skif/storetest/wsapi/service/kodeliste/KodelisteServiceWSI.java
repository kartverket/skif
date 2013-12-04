package no.statkart.skif.storetest.wsapi.service.kodeliste;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface KodelisteServiceWSI extends ServiceWSI {
    public KodelisteTransfer getKodeliste(@WebParam(name = "kodeIdClassName") String kodeIdClassName, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "context") StoreTestContext context) throws ServiceException;

    public KodelisteTransfer getKodelister(@WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "context") StoreTestContext context) throws ServiceException;
}
