package no.statkart.skif.storetest.wsapi.service.locking;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@WebService(
        name = "LockingTestService",
        serviceName = "LockingTestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/locking")
public class LockingTestServiceWSBean extends SkifWebService<LockingTestServiceWSI> implements LockingTestServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private LockingTestServiceWSI wsServiceChain;

    public LockingTestServiceWSBean() {
        super(LockingTestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public void lock(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.lock(bubbleId, context);
    }

    @Override
    @WebMethod
    public void update(@WebParam(name = "importantNumber") int importantNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.update(importantNumber, context);
    }

    @Override
    @WebMethod
    public void fail(@WebParam(name = "badNumber") int badNumber, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.fail(badNumber, context);
    }

    @Override
    @WebMethod
    public boolean isLockedByMe(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.isLockedByMe(bubbleId, context);
    }

    @Override
    @WebMethod
    public void releaseAllLocks(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.releaseAllLocks(context);
    }

    @Override
    @WebMethod
    public void loseALock(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.loseALock(context);
    }

    @Override
    @WebMethod
    public void nonTransactionalLockingFail(@WebParam(name = "bubbleId") StoreTestBubbleId bubbleId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.nonTransactionalLockingFail(bubbleId, context);
    }

    @Override
    @WebMethod
    public void nonTransactionalUnlockingFail(@WebParam(name = "unlockId") StoreTestBubbleId unlockId, @WebParam(name = "lockUnlockId") StoreTestBubbleId lockUnlockId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.nonTransactionalUnlockingFail(unlockId, lockUnlockId, context);
    }

    @Override
    @WebMethod
    public void nonTransactionalUnlocking(@WebParam(name = "unlockId") StoreTestBubbleId unlockId, @WebParam(name = "lockUnlockId") StoreTestBubbleId lockUnlockId, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.nonTransactionalUnlocking(unlockId, lockUnlockId, context);
    }
}
