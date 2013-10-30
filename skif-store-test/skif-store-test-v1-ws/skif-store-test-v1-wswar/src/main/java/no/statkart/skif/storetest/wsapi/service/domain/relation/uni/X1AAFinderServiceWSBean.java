package no.statkart.skif.storetest.wsapi.service.domain.relation.uni;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdForX1CCManyIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdSetForX1BBOneIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1BBOneIdCollection;
import no.statkart.skif.storetest.wsapi.domain.basic.X1CCManyIdCollection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

/**
 * @author Thomas Berg
 */

@WebService(
        name = "X1AAFinderService",
        serviceName = "X1AAFinderServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/domain/relation/uni/x1aafinder")
public class X1AAFinderServiceWSBean extends SkifWebService<X1AAFinderServiceWSI> implements X1AAFinderServiceWSI{

    @Resource
    private WebServiceContext ctx;

    private X1AAFinderServiceWSI wsServiceChain;

    public X1AAFinderServiceWSBean() {
        super(X1AAFinderServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public X1AAIdSetForX1BBOneIdMap findInvSomeBBIds(@WebParam(name = "x1BBOneIds") X1BBOneIdCollection x1BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findInvSomeBBIds(x1BBOneIds,storeTestContext);
    }

    @Override
    @WebMethod
    public X1AAIdForX1CCManyIdMap findInvSomeCCsId(@WebParam(name = "x1CCManyIds") X1CCManyIdCollection x1CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findInvSomeCCsId(x1CCManyIds,storeTestContext);
    }
}
