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
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@WebService(
        name = "StoreService",
        serviceName = "StoreServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/store")
public class StoreServiceWSBean extends SkifWebService<StoreServiceWSI> implements StoreServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private StoreServiceWSI wsServiceChain;

    public StoreServiceWSBean() {
        super(StoreServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public StoreTestBubble getObject(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return  wsServiceChain.getObject(id, context);
    }



    @Override
    @WebMethod
    public StoreTestBubbleList getObjects(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getObjects(ids, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleList getObjectsIgnoreMissing(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getObjectsIgnoreMissing(ids, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdList getVersions(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") XMLGregorianCalendar start, @WebParam(name = "end") XMLGregorianCalendar end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersions(id, start, end, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "start") XMLGregorianCalendar start, @WebParam(name = "end") XMLGregorianCalendar end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersionsForList(ids, start, end, context);
    }

    @Override
    @WebMethod
    public StoreTestBubble lock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException{
        return wsServiceChain.lock(id, context);
    }

    @Override
    @WebMethod
    public void unlock(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        wsServiceChain.unlock(id, context);
    }

    @Override
    @WebMethod
    public boolean isLocked(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.isLocked(id, context);
    }

}

