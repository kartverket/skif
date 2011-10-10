package no.statkart.skif.storetest.wsapi.service.histtest;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.BarFoosIdList;
import no.statkart.skif.storetest.wsapi.domain.BarId;
import no.statkart.skif.storetest.wsapi.domain.FooIdList;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.service.store.HistorikkServiceWSI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
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


    @Override
    @WebMethod
    public FooIdList findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion) {
        return wsServiceChain.findFooIdsForNavn(navn, snapshotVersion);
    }

    @Override
    @WebMethod
    public BarFoosIdList findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion) {
        return findBarFoosIdsSomInneholderFooMedNavn(navn, snapshotVersion);
    }

    @Override
    @WebMethod
    public BarFoosIdList findBarFoosIdsMedBarOgFoo(String fooNavn, BarId barId) {
        return findBarFoosIdsMedBarOgFoo(fooNavn, barId);
    }
}
