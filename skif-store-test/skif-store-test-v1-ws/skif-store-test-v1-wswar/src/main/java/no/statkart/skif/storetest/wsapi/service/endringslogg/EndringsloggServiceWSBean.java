package no.statkart.skif.storetest.wsapi.service.endringslogg;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Bobleklasse;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.EndringId;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringer;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.ReturnerBobler;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Thomas Berg
 * @author Henrik Fredholm
 * @since 2.4
 */
@SuppressWarnings("unused")

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
    public EndringId findSisteEndringId(@WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findSisteEndringId(storeTestContext);
    }

    @Override
    @WebMethod
    public Endringer findEndringer(@WebParam(name = "id") EndringId id, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "retunerBobler") ReturnerBobler retunerBobler, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findEndringer(id, bobleklasse, filter, retunerBobler, maksAntall, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcEndringskontroll(@WebParam(name = "id") EndringId id, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcEndringskontroll(id, bobleklasse, filter, maksAntall, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcObjektkontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcObjektkontrollForList(ids, bobleklasse, storeTestContext);
    }
}
