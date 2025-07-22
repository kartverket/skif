package no.statkart.skif.skiftest.wsapi.service.testb;

import com.google.inject.Injector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
@WebService(
        name = "BService",
        serviceName = "BServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testb")
public class BServiceWSBean extends SkifWebService<BServiceWSI> implements BServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private BServiceWSI wsServiceChain;


    public BServiceWSBean() {
        super(BServiceWSI.class);
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
