package no.statkart.skif.storetest.wsapi.service.domain.relation.uni.comp;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.storetest.wsapi.config.StoreTestWebServiceInjectorConfig;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.*;
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
        name = "X2AAWithEntityComponentFinderService",
        serviceName = "X2AAWithEntityComponentFinderServiceWS",
        targetNamespace = "http://skif.statkart.no/storetest/wsapi/service/domain/relation/uni/comp")
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
    public X2AAWithEntityComponentIdListForX2BBOneIdMap findInvSomeBBIds(@WebParam(name = "x2BBOneIds") X2BBOneIdList x2BBOneIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException {
        return wsServiceChain.findInvSomeBBIds(x2BBOneIds,storeTestContext);
    }

    @Override
    @WebMethod
    public X2AAWithEntityComponentIdForX2CCManyIdMap findInvSomeCCsId(@WebParam(name = "x2CCManyIds") X2CCManyIdList x2CCManyIds, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException{
        return wsServiceChain.findInvSomeCCsId(x2CCManyIds,storeTestContext);
    }
}
