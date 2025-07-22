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
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersionToStoreTestBubbleIdMap;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubble;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdToSnapshotBubbleIdsMap;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

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
    public SnapshotVersionToStoreTestBubbleIdMap getVersions(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") Timestamp start, @WebParam(name = "end") Timestamp end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersions(id, start, end, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdToSnapshotBubbleIdsMap getVersionsForList(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "start") Timestamp start, @WebParam(name = "end") Timestamp end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersionsForList(ids, start, end, context);
    }

}

