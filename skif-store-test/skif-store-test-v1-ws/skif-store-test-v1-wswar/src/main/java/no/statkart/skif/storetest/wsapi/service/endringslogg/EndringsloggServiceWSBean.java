package no.statkart.skif.storetest.wsapi.service.endringslogg;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Domeneklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.EndringList;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringsklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Thomas Berg
 * @author Henrik Fredholm
 * @since 2.4
 */
@WebService(
        name = "EndringsloggService",
        serviceName = "EndringsloggServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/endringslogg")
public class EndringsloggServiceWSBean extends SkifWebService<EndringsloggServiceWSI> implements EndringsloggServiceWSI {


    @Resource
    private WebServiceContext ctx;

    private EndringsloggServiceWSI wsServiceChain;

    public EndringsloggServiceWSBean() {
        super(EndringsloggServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public long findSisteEndringsnummer(@WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findSisteEndringsnummer(snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public EndringList findEndringerEtterEndringsnummer(@WebParam(name = "endringsnummer") long endringsnummer, @WebParam(name="endringsklasse") Endringsklasse endringsklasse, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findEndringerEtterEndringsnummer(endringsnummer, endringsklasse, maksAntall,snapshotVersion,storeTestContext);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdList findIdsEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findIdsEtterId(id, domeneklasse, maksAntall, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcKontrollForRange(@WebParam(name = "fraId") StoreTestBubbleId fraId, @WebParam(name = "tilId") StoreTestBubbleId tilId, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcKontrollForRange(fraId, tilId, domeneklasse, snapshotVersion, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcKontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name="domeneklasse") Domeneklasse domeneklasse, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcKontrollForList(ids, domeneklasse, snapshotVersion, storeTestContext);
    }
}
