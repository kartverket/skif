package no.statkart.skif.storetest.wsapi.service.endringslogg;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
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
    public Kontroll calcEndringskontroll(@WebParam(name = "id") EndringId id, @WebParam(name="bobleklasse") Bobleklasse bobleklasse, @WebParam(name="filter") String filter, @WebParam(name="maksAntall") int maksAntall, @WebParam(name="storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcEndringskontroll(id, bobleklasse, filter, maksAntall, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcObjektkontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcObjektkontrollForList(ids, bobleklasse, storeTestContext);
    }
}
