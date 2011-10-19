package no.statkart.skif.storetest.wsapi.service.histtest;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.w.*;

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
        name = "HistTestService",
        serviceName = "HistTestServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/histtest")
public class HistTestServiceWSBean extends SkifWebService<HistTestServiceWSI> implements HistTestServiceWSI {

    @Resource
    private WebServiceContext ctx;

    private HistTestServiceWSI wsServiceChain;


    public HistTestServiceWSBean() {
        super(HistTestServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    public String  hello() { return null;}
    public StoreTestBubbleIdx hello1() { return null;}

    @Override
    @WebMethod
    public FooIdList findFooIdsForNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findFooIdsForNavn(navn, snapshotVersion, storeTestContext);
    }

    @Override
    public BarFoosIdList findBarFoosIdsSomInneholderFooMedNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findBarFoosIdsSomInneholderFooMedNavn(navn, snapshotVersion, storeTestContext);
    }

    @Override
    public BarFoosIdList findBarFoosIdsMedBarOgFoo(@WebParam(name = "fooNavn") String fooNavn, @WebParam(name = "barId") BarId barId, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findBarFoosIdsMedBarOgFoo(fooNavn, barId, storeTestContext);
    }
}
