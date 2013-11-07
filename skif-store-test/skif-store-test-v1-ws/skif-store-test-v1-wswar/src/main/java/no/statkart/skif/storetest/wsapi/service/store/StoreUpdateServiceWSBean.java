package no.statkart.skif.storetest.wsapi.service.store;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@WebService(
        name = "StoreUpdateService",
        serviceName = "StoreUpdateServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/store")
public class StoreUpdateServiceWSBean extends SkifWebService<StoreUpdateServiceWSI> implements StoreUpdateServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private StoreUpdateServiceWSI wsServiceChain;

    public StoreUpdateServiceWSBean() {
        super(StoreUpdateServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreTestBubble lockObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.lockObject(id, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleList lockObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.lockObjects(ids, context);
    }

    @Override
    @WebMethod
    public void saveTransfer(@WebParam(name = "transfer") UnitOfWorkTransfer transfer, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.saveTransfer(transfer, context);
    }

}
