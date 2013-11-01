package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.comp;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdForX1CCManyIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1AAIdListForX1BBOneIdMap;
import no.statkart.skif.storetest.wsapi.domain.basic.X1BBOneIdList;
import no.statkart.skif.storetest.wsapi.domain.basic.X1CCManyIdList;

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
        name = "X2AAWithEntityComponentFinderService",
        serviceName = "X1AAWithEntityComponentFinderServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/domain/relation/uni/component/entity")
public class X2AAWithEntityComponentFinderServiceWSBean extends SkifWebService<X2AAWithEntityComponentFinderServiceWSI> implements X2AAWithEntityComponentFinderServiceWSI{

    @Resource
    private WebServiceContext ctx;

    private X2AAWithEntityComponentFinderServiceWSI wsServiceChain;

    public X2AAWithEntityComponentFinderServiceWSBean() {
        super(X2AAWithEntityComponentFinderServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = StoreTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public X1AAIdListForX1BBOneIdMap findInvSomeBBIds(@WebParam(name = "x1BBOneIds") X1BBOneIdList x1BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findInvSomeBBIds(x1BBOneIds,storeTestContext);
    }

    @Override
    @WebMethod
    public X1AAIdForX1CCManyIdMap findInvSomeCCsId(@WebParam(name = "x1CCManyIds") X1CCManyIdList x1CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) {
        return wsServiceChain.findInvSomeCCsId(x1CCManyIds,storeTestContext);
    }
}
