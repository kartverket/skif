package no.statkart.skif.storetest.wsapi.service.nedlastning;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.*;
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
@WebService(
        name = "NedlastningService",
        serviceName = "NedlastningServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/nedlastning")
public class NedlastningServiceWSBean extends SkifWebService<NedlastningServiceWSI> implements NedlastningServiceWSI {

    @Resource
    private WebServiceContext ctx;

    private NedlastningServiceWSI wsServiceChain;

    public NedlastningServiceWSBean() {
        super(NedlastningServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdList findIdsEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "Bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findIdsEtterId(id, bobleklasse, filter, maksAntall, storeTestContext);
    }

    @Override
    @WebMethod
    public StoreTestBubbleList findObjekterEtterId(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "Bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "maksAntall") int maksAntall, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findObjekterEtterId(id, bobleklasse, filter, maksAntall, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcObjektkontrollForRange(@WebParam(name = "fraId") StoreTestBubbleId fraId, @WebParam(name = "tilId") StoreTestBubbleId tilId, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "filter") String filter, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcObjektkontrollForRange(fraId, tilId, bobleklasse, filter, storeTestContext);
    }

    @Override
    @WebMethod
    public Kontroll calcObjektkontrollForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "bobleklasse") Bobleklasse bobleklasse, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.calcObjektkontrollForList(ids, bobleklasse, storeTestContext);
    }
}
