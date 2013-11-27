package no.statkart.skif.storetest.wsapi.service.endringslogg;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Domeneklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.EndringList;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringsklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author Thomas Berg
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface EndringsloggServiceWSI extends ServiceWSI {
    public long findSisteEndringsnummer(@WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public EndringList findEndringerEtterEndringsnummer(@WebParam(name = "endringsnummer") long endringsnummer, @WebParam(name="endringsklasse") Endringsklasse endringsklasse, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public StoreTestBubbleIdList findIdsEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public Kontroll calcKontrollForRange(@WebParam(name = "fraId") StoreTestBubbleId fraId, @WebParam(name = "tilId") StoreTestBubbleId tilId, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
    public Kontroll calcKontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException;
}
