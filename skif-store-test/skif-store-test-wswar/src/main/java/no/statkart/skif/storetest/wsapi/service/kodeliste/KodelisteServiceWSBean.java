package no.statkart.skif.storetest.wsapi.service.kodeliste;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.*;
import no.statkart.skif.storetest.wsapi.domain.ktest.MyList4;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import no.statkart.skif.storetest.wsapi.service.store.StoreServiceWSI;

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
        name = "KodelisteService",
        serviceName = "KodelisteServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/kodeliste")
public class KodelisteServiceWSBean extends SkifWebService<KodelisteServiceWSI> implements KodelisteServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private KodelisteServiceWSI wsServiceChain;

    public KodelisteServiceWSBean() {
        super(KodelisteServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public KodelisteIdList getKodelisteIds(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        return wsServiceChain.getKodelisteIds(context);
    }

    @Override
    @WebMethod
    public KodelisteTransfer getKodelister(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = wsServiceChain.getKodelister(context);
        KodeIdList value = new KodeIdList();
        KodeId kodeId = transfer.getKodeIds().getItem().get(0);
        kodeId.setValue("10");

        SnapshotVersion sv = new SnapshotVersion();
        sv.setTime(1000);
        sv.setNanos(100);

        kodeId.setSnapshotVersion(sv);
        value.getItem().add(kodeId);

        transfer.setKodeIds(value);
        transfer.setKodelisteIds(new KodelisteIdList());
        transfer.setObjects(new StoreTestBubbleList());
        return transfer;
    }

    @Override
    @WebMethod
    public KodeIdTestList getKodelisterTest(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = wsServiceChain.getKodelister(context);
        KodeId kodeId = transfer.getKodeIds().getItem().get(0);
        SnapshotVersion sv = new SnapshotVersion();
        sv.setTime(1000);
        sv.setNanos(100);

        kodeId.setSnapshotVersion(sv);
        kodeId.setValue("10");

        KodeIdTestList list = new KodeIdTestList();
        list.getItem().add("Hello");;
        return list;
    }

    @WebMethod
    public MyList getMyList(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        MyList myList = new MyList();
        myList.getItem().add("Hello1");
        return myList;
    }
    @WebMethod

    public MyList2 getMyList2(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        MyList2 myList = new MyList2();
        myList.getItem().add("Hello2");
        return myList;
    }

    public MyList3 getMyList3(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        MyList3 myList = new MyList3();
        myList.getItem().add("Hello3");
        return myList;
    }
    public MyList4 getMyList4(@WebParam(name = "context") StoreTestContext context) throws ServiceException {
        KodelisteTransfer transfer = new KodelisteTransfer();
        MyList4 myList = new MyList4();
        myList.getItem().add("Hello4");
        return myList;
    }

}

