package no.statkart.skif.storetest.wsapi.service.lock;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import no.statkart.skif.storetest.wsapi.service.store.StoreServiceWSI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

@WebService(
        name = "LockService",
        serviceName = "LockServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/lock")
public class LockServiceWSBean extends SkifWebService<LockServiceWSI> implements LockServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private LockServiceWSI wsServiceChain;

    public LockServiceWSBean() {
        super(LockServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreTestBubble lock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException{
        return wsServiceChain.lock(id, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleList lockForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.lockForList(ids, context);
    }

    @Override
    @WebMethod
    public void unlock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.unlock(id, context);
    }

    @Override
    @WebMethod
    public void unlockForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.unlockForList(ids, context);
    }

    @Override
    @WebMethod
    public boolean isLocked(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.isLocked(id, context);
    }

}

