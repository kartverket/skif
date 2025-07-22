package no.statkart.skif.storetest.wsapi.service.store;

import com.google.inject.Injector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubble;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.UnitOfWorkTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

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
