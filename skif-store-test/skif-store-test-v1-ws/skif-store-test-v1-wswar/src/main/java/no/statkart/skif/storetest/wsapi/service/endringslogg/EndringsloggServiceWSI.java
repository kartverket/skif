package no.statkart.skif.storetest.wsapi.service.endringslogg;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.EndringList;
import no.statkart.skif.storetest.wsapi.domain.basic.Endringsklasse;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Thomas Berg
 */
public interface EndringsloggServiceWSI extends ServiceWSI {
    public long findSisteEndringsnummer(@WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public EndringList findEndringerEtterEndringsnummer(@WebParam(name = "endringsnummer") long endringsnummer, @WebParam(name="endringsklasse") Endringsklasse endringsklasse, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
