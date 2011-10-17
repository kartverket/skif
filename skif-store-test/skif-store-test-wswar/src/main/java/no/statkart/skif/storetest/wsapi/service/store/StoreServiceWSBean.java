package no.statkart.skif.storetest.wsapi.service.store;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 1.1
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
    public StoreTestBubbleIdList getVersionsX(@WebParam(name = "id") StoreTestBubbleId id, @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersionsX(id, start, end, context);
    }

    @Override
    @WebMethod
    public StoreTestBubbleIdListForStoreTestBubbleIdsMap getVersionsForListX(@WebParam(name = "ids") StoreTestBubbleIdList ids, @WebParam(name = "start") SnapshotVersion start, @WebParam(name = "end") SnapshotVersion end, @WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getVersionsForListX(ids, start, end, context);
    }

/*
    @WebMethod
    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer getTransfer(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        KodeIdList kodeIds = new KodeIdList();
        KodeId kodeId = new KodeId();
        kodeId.setValue("2");
        kodeId.setSnapshotVersion(new SnapshotVersion());
        kodeIds.getItem().add(kodeId);
        transfer.setKodeIds(kodeIds);
        return transfer;
    }
  */
    @WebMethod
    public MyList getMyList(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        MyList myList = new MyList();
        myList.getItem().add("Hello1");
        return myList;
    }

}

