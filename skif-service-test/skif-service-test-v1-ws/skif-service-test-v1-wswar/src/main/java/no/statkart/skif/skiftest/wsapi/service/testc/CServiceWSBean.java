package no.statkart.skif.skiftest.wsapi.service.testc;

import com.google.inject.Injector;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
@WebService(
        name = "CService",
        serviceName = "CServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testc")
public class CServiceWSBean extends SkifWebService<CServiceWSI> implements CServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private CServiceWSI wsServiceChain;


    public CServiceWSBean() {
        super(CServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    @WebMethod
    public String m1(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m1(callSpec);
    }

    @Override
    @WebMethod
    public String m2(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m2(callSpec);
    }

    @Override
    @WebMethod
    public String m3(@WebParam(name = "callSpec") StringList callSpec) throws ServiceException {
        return wsServiceChain.m3(callSpec);
    }

}
