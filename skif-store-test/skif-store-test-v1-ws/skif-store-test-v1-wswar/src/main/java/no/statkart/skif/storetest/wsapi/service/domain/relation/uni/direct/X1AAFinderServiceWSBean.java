package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.direct;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

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
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/domain/relation/uni/direct")
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
    public X1AAIdListForX1BBOneIdMap findInvSomeBBIds(@WebParam(name = "x1BBOneIds") X1BBOneIdList x1BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException  {
        return wsServiceChain.findInvSomeBBIds(x1BBOneIds,storeTestContext);
    }

    @Override
    @WebMethod
    public X1AAIdForX1CCManyIdMap findInvSomeCCsId(@WebParam(name = "x1CCManyIds") X1CCManyIdList x1CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findInvSomeCCsId(x1CCManyIds,storeTestContext);
    }
}
